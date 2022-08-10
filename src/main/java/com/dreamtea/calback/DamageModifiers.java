package com.dreamtea.calback;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * A damage modifier is returned on Callbacks for EntityDamage and EntityFallDamage
 * @param items the items that should not be allowed to process until damage calculations are complete. \
 *              These items are essentially removed from the players inventory for the duration
 * @param scale a multiplier to apply to the damage dealt;
 */
public record DamageModifiers(List<ItemStack> items, float scale) {
  public DamageModifiers(float scale) {
    this(List.of(), scale);
  }
  public DamageModifiers() {
    this(List.of(), 1);
  }
}
