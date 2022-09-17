package com.dreamtea.effect;

import com.dreamtea.imixin.IDisableItemStacks;
import com.dreamtea.imixin.IHaveOriginalDurability;
import com.dreamtea.tag.TagUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

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

  public static float proportionalDurability(ItemStack stack, boolean down){
    int realDurability;
    int fakeDurability;
    int currentDamage = stack.getDamage();;
    if(stack.getItem() instanceof ArmorItem armor) {
      realDurability = armor.getMaterial().getDurability(armor.getSlotType());
      fakeDurability = ((IHaveOriginalDurability)armor.getMaterial()).getOriginalDurability(armor.getSlotType());
    } else if(stack.getItem() instanceof ToolItem tool){
      realDurability = tool.getMaterial().getDurability();
      fakeDurability = ((IHaveOriginalDurability)tool.getMaterial()).getOriginalDurability(null);
    } else {
      return 0;
    }
    float scaleDown = ((float)fakeDurability) / realDurability;
    if(down){
      return currentDamage * scaleDown;
    }
    return currentDamage / scaleDown;
  }
}
