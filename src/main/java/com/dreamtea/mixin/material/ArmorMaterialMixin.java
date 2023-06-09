package com.dreamtea.mixin.material;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorMaterials.class)
public class ArmorMaterialMixin{

  @Shadow @Final public static ArmorMaterials NETHERITE;

  @Shadow @Final public static ArmorMaterials DIAMOND;

  @Inject(method = "getDurability", at = @At("HEAD"), cancellable = true)
  public void netheriteAsToughAsDiamond(ArmorItem.Type type, CallbackInfoReturnable<Integer> cir){
    if(this.equals(NETHERITE)){
      cir.setReturnValue(DIAMOND.getDurability(type));
    }
  }

}
