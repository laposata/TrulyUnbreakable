package com.dreamtea.tag;

import com.dreamtea.effect.IndestructibleEffect;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.dreamtea.TrulyUnbreakable.NAMESPACE;

public class IndestructibleTag extends FabricTagProvider.ItemTagProvider {

  public static TagKey<Item> INDESTRUCTIBLE_ITEMS = TagUtils.createItemTag(new Identifier(NAMESPACE, "indestructible"));

  public static final List<Item> indestructibleDefaults = List.of(
    Items.NETHERITE_AXE, Items.NETHERITE_HOE, Items.NETHERITE_PICKAXE,
    Items.NETHERITE_SHOVEL, Items.NETHERITE_SWORD, Items.NETHERITE_CHESTPLATE,
    Items.NETHERITE_HELMET, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS,
    Items.ELYTRA, Items.TRIDENT
  );

  public IndestructibleTag(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
    super(output, completableFuture);
  }


  /**
   * Adds The custom data hook to all items.
   * This is added to all items so that if an item beyond the list above gets indestructible,
   * they'll still function correctly.
   */
  public static void addDamageHookToAllItems(){
    Registries.ITEM.stream().forEach(i -> ((ItemExtensions)i).fabric_setCustomDamageHandler(IndestructibleEffect::doNotBreak));
  }

  @Override
  protected void configure(RegistryWrapper.WrapperLookup arg) {
    getOrCreateTagBuilder(INDESTRUCTIBLE_ITEMS).add(indestructibleDefaults.toArray(Item[]::new));
  }
}
