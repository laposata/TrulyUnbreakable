package com.dreamtea.tag;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TagUtils {
    public static boolean itemIsIn(ItemStack item, TagKey<Item> key){
        return Registry.ITEM.getOrCreateEntry(Registry.ITEM.getKey(item.getItem()).get()).isIn(key);
    }

    public static TagKey<Item> createItemTag(Identifier tag){
        return TagKey.of(Registry.ITEM_KEY,tag);
    }


}
