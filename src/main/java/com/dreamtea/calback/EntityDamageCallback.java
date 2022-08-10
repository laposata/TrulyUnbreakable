package com.dreamtea.calback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public interface EntityDamageCallback {

  Event<EntityDamageCallback> EVENT = EventFactory.createArrayBacked(EntityDamageCallback.class,
    (listeners) -> (entity, source, amount) -> {
      for (EntityDamageCallback listener : listeners) {
        TypedActionResult<DamageModifiers> result = listener.interact(entity, source, amount);

        if(result.getResult() != ActionResult.PASS) {
          return result;
        }
      }
      return TypedActionResult.pass(new DamageModifiers(amount));
    });

  TypedActionResult<DamageModifiers> interact(LivingEntity entity, DamageSource source, float amount);

}
