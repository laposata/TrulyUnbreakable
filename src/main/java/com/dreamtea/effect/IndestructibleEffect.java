package com.dreamtea.effect;

import com.dreamtea.imixin.IDisableItemStacks;
import com.dreamtea.tag.TagUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

import static com.dreamtea.imixin.IDisableItemStacks.getBroken;
import static com.dreamtea.tag.IndestructibleTag.INDESTRUCTIBLE_ITEMS;
public class IndestructibleEffect {

  public static boolean isNotUsable(ItemStack stack) {
    return stack.getDamage() >= stack.getMaxDamage();
  }

  public static boolean isIndestructible(ItemStack item){
    return (TagUtils.itemIsIn(item, INDESTRUCTIBLE_ITEMS) || IDisableItemStacks.isIndestructible(item)) && item.isDamageable();
  }

  public static int doNotBreak(ItemStack stack, int amount, LivingEntity entity, Consumer<LivingEntity> breakCallback) {
    if(!isIndestructible(stack)){
      return amount;
    }
    int i = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);
    int reduced = 0;
    for (int k = 0; i > 0 && k < amount; ++k) {
      if (!UnbreakingEnchantment.shouldPreventDamage(stack, i, entity.getRandom())) continue;
      ++reduced;
    }
    if ((amount -= reduced) <= 0) {
      return 0;
    }
    stack.setDamage(Math.min(stack.getMaxDamage(), stack.getDamage() + amount));
    if(isNotUsable(stack) && !getBroken(stack)){
      ((IDisableItemStacks)(Object) stack).breakItem();
      breakCallback.accept(entity);
    }
    return 0;
  }
}
