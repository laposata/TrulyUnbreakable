package com.dreamtea;

import com.dreamtea.tag.IndestructibleTag;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DatagenEntry implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
      fabricDataGenerator.addProvider(IndestructibleTag::new);
    }
}
