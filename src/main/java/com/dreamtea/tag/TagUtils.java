package com.dreamtea.tag;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class TagUtils {
    public static boolean itemIsIn(ItemStack item, TagKey<Item> key){
        Optional<RegistryEntryList.Named<Item>> entryList = Registries.ITEM.getEntryList(key);
        if(entryList.isEmpty()) return false;
        return entryList.get().contains(item.getRegistryEntry());
    }

    public static TagKey<Item> createItemTag(Identifier tag){
        return TagKey.of(RegistryKeys.ITEM, tag);
    }


}
