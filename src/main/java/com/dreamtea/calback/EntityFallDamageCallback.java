package com.dreamtea.calback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public interface EntityFallDamageCallback {
  Event<EntityFallDamageCallback> EVENT = EventFactory.createArrayBacked(EntityFallDamageCallback.class,
    (listeners) -> (entity, distance, multiplier, source) -> {
      for (EntityFallDamageCallback listener : listeners) {
        TypedActionResult<DamageModifiers> result = listener.interact(entity, distance, multiplier, source);

        if(result.getResult() != ActionResult.PASS) {
          return result;
        }
      }
      return TypedActionResult.pass(new DamageModifiers(1));
    });

  TypedActionResult<DamageModifiers> interact(LivingEntity entity, float fallDistance, float damageMultiplier, DamageSource damageSource);
}
