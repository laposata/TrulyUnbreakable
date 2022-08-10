package com.dreamtea.mixin;

import com.dreamtea.calback.DamageModifiers;
import com.dreamtea.calback.EntityDamageCallback;
import com.dreamtea.calback.EntityFallDamageCallback;
import com.dreamtea.imixin.IDisableItemStacks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

  Map<DamageSource, List<ItemStack>> inProgressDamage= new HashMap<>();

  @Inject(method = "damage", at=@At("HEAD"), cancellable = true)
  public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
    //If this damage source has already run through here, skip all this
    if(!inProgressDamage.containsKey(source)) {
      //Grabbing this for ease and calling the callback
      LivingEntity entity = (LivingEntity) (Object) this;
      TypedActionResult<DamageModifiers> result =
        EntityDamageCallback.EVENT.invoker().interact(entity, source, amount);

      //If the call failed disable all items returned
      if (result.getResult() == ActionResult.FAIL) {
        DamageModifiers modifier = result.getValue();
        inProgressDamage.put(source, modifier.items());
        modifier.items().forEach(itemStack -> ((IDisableItemStacks) (Object) itemStack).disable());
      } else {
        //Even if it doesn't fail, Still log the damageSource
        inProgressDamage.put(source, List.of());
      }
      cir.setReturnValue(entity.damage(source, amount * result.getValue().scale()));
      inProgressDamage.remove(source).forEach(itemStack -> ((IDisableItemStacks)(Object)itemStack).enable());
    }
  }

  @Inject(method ="handleFallDamage", at= @At("HEAD"), cancellable = true)
  public void onFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir){
    //If this damage source has already run through here, skip all this
    if(!inProgressDamage.containsKey(damageSource)) {
      //Grabbing this for ease and calling the callback
      LivingEntity entity = (LivingEntity) (Object) this;
      TypedActionResult<DamageModifiers> result =
        EntityFallDamageCallback.EVENT.invoker().interact(entity, fallDistance, damageMultiplier, damageSource);
      //If the call failed disable all items returned
      if (result.getResult() == ActionResult.FAIL) {
        DamageModifiers modifier = result.getValue();
        inProgressDamage.put(damageSource, modifier.items());
        modifier.items().forEach(itemStack -> {
          ((IDisableItemStacks) (Object) itemStack).disable();
        });
      } else {
        //Even if it doesn't fail, Still log the damageSource
        inProgressDamage.put(damageSource, List.of());
      }
      cir.setReturnValue(entity.handleFallDamage(fallDistance, damageMultiplier * result.getValue().scale(), damageSource));
      inProgressDamage.remove(damageSource).forEach(itemStack -> ((IDisableItemStacks)(Object)itemStack).enable());
    }
  }

}
