package com.dreamtea.mixin.material;

import com.dreamtea.imixin.IHaveOriginalDurability;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ToolMaterials;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolMaterials.class)
public class ToolMaterialsMixin implements IHaveOriginalDurability {

  @Shadow @Final public static ToolMaterials NETHERITE;

  @Shadow @Final public static ToolMaterials DIAMOND;

  @Inject(method = "getDurability", at = @At("HEAD"), cancellable = true)
  public void netheriteAsToughAsDiamond(CallbackInfoReturnable<Integer> cir){
    if(this.equals(NETHERITE)){
      cir.setReturnValue(DIAMOND.getDurability());
    }
  }

  @Override
  public int getOriginalDurability(EquipmentSlot slot) {
    return ((ToolMaterials)(Object)this).getDurability();
  }
}
