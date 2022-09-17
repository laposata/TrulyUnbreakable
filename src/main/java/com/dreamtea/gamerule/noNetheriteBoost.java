package com.dreamtea.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class noNetheriteBoost {
  public static final String NO_NETHERITE_BOOST_RULE = "noNetheriteBoost";
  public static GameRules.Key<GameRules.BooleanRule> NO_NETHERITE_BOOST;

  public static void initRule(){
    NO_NETHERITE_BOOST = GameRuleRegistry.register(NO_NETHERITE_BOOST_RULE, GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
  }
}
