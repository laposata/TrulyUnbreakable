package com.dreamtea.effect;

import com.dreamtea.imixin.IDisableItemStacks;
import com.dreamtea.tag.TagUtils;
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
    return TagUtils.itemIsIn(item, INDESTRUCTIBLE_ITEMS) && item.isDamageable();
  }

  public static int doNotBreak(ItemStack stack, int amount, LivingEntity entity, Consumer<LivingEntity> breakCallback) {
    if(!isIndestructible(stack)){
      return amount;
    }
    stack.setDamage(Math.min(stack.getMaxDamage(), stack.getDamage() + amount));
    if(isNotUsable(stack) && !getBroken(stack)){
      ((IDisableItemStacks)(Object) stack).breakItem();
      breakCallback.accept(entity);
    }
    return 0;
  }
}
