package com.dreamtea.mixin.tools;

import com.dreamtea.imixin.IInteractingTool;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(HoeItem.class)
public class HoeItemMixin implements IInteractingTool {
  @Shadow @Final protected static Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLING_ACTIONS;

  @Override
  public List<Block> getBlocks() {
    return TILLING_ACTIONS.keySet().stream().toList();
  }
}
