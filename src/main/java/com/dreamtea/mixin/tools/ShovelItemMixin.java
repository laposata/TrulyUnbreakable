package com.dreamtea.mixin.tools;

import com.dreamtea.imixin.IInteractingTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.block.Blocks.CAMPFIRE;

@Mixin(ShovelItem.class)
public class ShovelItemMixin implements IInteractingTool {

  @Shadow @Final protected static Map<Block, BlockState> PATH_STATES;

  @Override
  public List<Block> getBlocks() {
    List<Block> blocks = new ArrayList<> (PATH_STATES.keySet());
    blocks.add(CAMPFIRE);
    return blocks;
  }
}
