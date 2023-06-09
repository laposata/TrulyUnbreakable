package com.dreamtea.mixin;

import com.dreamtea.effect.IndestructibleEffect;
import com.dreamtea.imixin.IDisableItemStacks;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.enchantment.VanishingCurseEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IDisableItemStacks {

  @Shadow private NbtCompound nbt;
  @Shadow public abstract NbtCompound getOrCreateNbt();

  @Shadow public abstract int getDamage();

  @Override
  public boolean getBroken(){
     return this.nbt != null && this.nbt.getBoolean(BROKEN_IDENTIFIER);
  }

  private void setBroken(boolean broken){
    this.getOrCreateNbt().putBoolean(BROKEN_IDENTIFIER, broken);
  }

  public void breakItem(){
    setBroken(true);
  }

  public void repair(){
    setBroken(false);
  }

  @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
  public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
    if(this.getBroken()){
      cir.setReturnValue(ActionResult.PASS);
    }
  }

  @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
  public void useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir){
    if(this.getBroken()){
      cir.setReturnValue(ActionResult.PASS);
    }
  }

  @Inject(method = "getAttributeModifiers", at = @At("HEAD"), cancellable = true)
  public void getAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
    if(this.getBroken()){
      cir.setReturnValue(ImmutableMultimap.of());
    }
  }

  @Inject(method ="setDamage", at=@At("HEAD"))
  public void onRepair(int damage, CallbackInfo ci){
    if(this.getBroken() && damage < this.getDamage()){
      this.repair();
    }
  }

  @Inject(method = "getMiningSpeedMultiplier", at = @At("HEAD"), cancellable = true)
  public void notFasterWhenBroken(BlockState state, CallbackInfoReturnable<Float> cir){
    if(this.getBroken()){
      cir.setReturnValue(1.0f);
    }
  }

  @Inject(method ="getEnchantments", at=@At("RETURN"), cancellable = true)
  public void noEnchantments(CallbackInfoReturnable<NbtList> cir){
    if(this.getBroken()){
      NbtList list = new NbtList();
      NbtList enchantments = cir.getReturnValue();
      for(int i = 0; i < enchantments.size(); i ++){
        NbtCompound nbtCompound = enchantments.getCompound(i);
        Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound))
          .ifPresent((e) -> {
            if(e instanceof MendingEnchantment
              || e instanceof VanishingCurseEnchantment)
            {
              list.add(nbtCompound);
              cir.setReturnValue(list);
              return;
            }
          });
      }
      cir.setReturnValue(list);
      return;
    }
  }

  @Inject(method = "isSuitableFor", at = @At("HEAD"), cancellable = true)
  public void isSuitableFor(BlockState state, CallbackInfoReturnable<Boolean> cir) {
    if(this.getBroken()){
      cir.setReturnValue(false);
    }
  }

  @Inject(method = "getDamage", at = @At("RETURN"))
  public void getBrokenNbt(CallbackInfoReturnable<Integer> cir){
    if(IndestructibleEffect.isIndestructible((ItemStack)(Object)this)){
      getBroken();
    }
  }

  @Inject(method = "setDamage", at = @At("RETURN"))
  public void setDamageNbt(int damage, CallbackInfo ci){
    if(IndestructibleEffect.isIndestructible((ItemStack)(Object)this)){
      setBroken(getBroken());
    }
  }

  @Inject(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At("RETURN"), cancellable = true)
  public void dontBreak(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir){
    cir.setReturnValue(cir.getReturnValue() && !IndestructibleEffect.isIndestructible((ItemStack)(Object)this));
  }

  @Redirect(method ="getTooltip", at=@At(value= "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantments()Lnet/minecraft/nbt/NbtList;"))
  private NbtList stillCollectEnchantsWhenBroken(ItemStack instance){
    NbtCompound nbt = instance.getNbt();
    return nbt != null ? nbt.getList("Enchantments", 10) : new NbtList();
  }

  @Redirect(method ="getTooltip", at=@At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
  public boolean addBrokenToTooltip(List instance, Object e){
    instance.add(e);
    if(this.getBroken()) {
      instance.add(Text.of("BROKEN").copy().formatted(Formatting.RED, Formatting.BOLD));
    }
    return true;
  }

}
