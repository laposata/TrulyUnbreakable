package com.dreamtea.mixin.tools;

import com.dreamtea.imixin.IInteractingTool;
import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(AxeItem.class)
public class AxeItemMixin implements IInteractingTool {

  @Shadow @Final
  protected static Map<Block, Block> STRIPPED_BLOCKS;

  @Override
  public List<Block> getBlocks() {
    return STRIPPED_BLOCKS.keySet().stream().toList();
  }
}
