package com.dreamtea.imixin;

import net.minecraft.item.ItemStack;

public interface IDisableItemStacks {

  default boolean isIndestructible(){
    return false;
  }

  String BROKEN_IDENTIFIER = "Broken";
  boolean isDisabled();
  boolean getBroken();
  void breakItem();
  void repair();

  static boolean getBroken(ItemStack stack){
    if(((Object)stack) instanceof IDisableItemStacks breakable){
      return breakable.getBroken();
    }
    return false;
  }

  static boolean isIndestructible(ItemStack stack){
    if(((Object)stack) instanceof IDisableItemStacks breakable){
      return breakable.isIndestructible();
    }
    return false;
  }
}
