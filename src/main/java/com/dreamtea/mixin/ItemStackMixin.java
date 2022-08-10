package com.dreamtea.mixin;

import com.dreamtea.effect.IndestructibleEffect;
import com.dreamtea.imixin.IDisableItemStacks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.enchantment.VanishingCurseEnchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
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

  @Shadow @Final private Item item;

  @Shadow public abstract boolean itemMatches(RegistryEntry<Item> itemEntry);

  private boolean disabled = false;

  @Override
  public void disable() {
    this.disabled = true;
  }

  @Override
  public void enable() {
    this.disabled = false;
  }

  @Override
  public boolean isDisabled() {
    return this.disabled;
  }

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

  @Inject(method ="getItem", at=@At("RETURN"), cancellable = true)
  public void getItem(CallbackInfoReturnable<Item> cir){
    if(this.isDisabled()){
      cir.setReturnValue(Items.AIR);
    }
  }

  @Inject(method ="setDamage", at=@At("HEAD"))
  public void onRepair(int damage, CallbackInfo ci){
    if(this.getBroken() && damage < this.getDamage()){
      this.repair();
    }
  }

  @Inject(method ="getEnchantments", at=@At("RETURN"), cancellable = true)
  public void noEnchantments(CallbackInfoReturnable<NbtList> cir){
    if(this.getBroken()){
      NbtList list = new NbtList();
      NbtList enchantments = cir.getReturnValue();
      for(int i = 0; i < enchantments.size(); i ++){
        NbtCompound nbtCompound = enchantments.getCompound(i);
        Registry.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound))
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

  @Redirect(method ="getTooltip", at=@At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
  public boolean addBrokenToTooltip(List instance, Object e){
    instance.add(e);
    if(this.getBroken()) {
      instance.add(Text.of("BROKEN").copy().formatted(Formatting.RED, Formatting.BOLD));
    }
    return true;
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
}