package com.dreamtea.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
  @Redirect(method ="get", at= @At(value= "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantments()Lnet/minecraft/nbt/NbtList;"))
  private static NbtList stillCollectEnchantsWhenBroken(ItemStack instance){
    NbtCompound nbt = instance.getNbt();
    return nbt != null ? nbt.getList("Enchantments", 10) : new NbtList();
  }
}
