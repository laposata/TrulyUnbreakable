package com.dreamtea.imixin;

import net.minecraft.item.ItemStack;

public interface IDisableItemStacks {

  String BROKEN_IDENTIFIER = "Broken";

  void disable();
  void enable();
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

  static void disable(ItemStack stack){
    if(((Object)stack) instanceof IDisableItemStacks breakable){
      breakable.disable();
    }
  }

  static void enable(ItemStack stack){
    if(((Object)stack) instanceof IDisableItemStacks breakable){
      breakable.enable();
    }
  }
}
