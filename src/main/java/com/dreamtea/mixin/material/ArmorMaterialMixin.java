package com.dreamtea.mixin.material;

import com.dreamtea.imixin.IHaveOriginalDurability;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorMaterials.class)
public class ArmorMaterialMixin implements IHaveOriginalDurability {

  @Shadow @Final public static ArmorMaterials NETHERITE;

  @Shadow @Final public static ArmorMaterials DIAMOND;

  @Inject(method = "getDurability", at = @At("HEAD"), cancellable = true)
  public void netheriteAsToughAsDiamond(EquipmentSlot slot, CallbackInfoReturnable<Integer> cir){
    if(this.equals(NETHERITE)){
      cir.setReturnValue(DIAMOND.getDurability(slot));
    }
  }

  @Override
  public int getOriginalDurability(EquipmentSlot slot) {
    return ((ArmorMaterials)(Object)this).getDurability(slot);
  }
}
