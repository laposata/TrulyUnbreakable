package com.dreamtea;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dreamtea.effect.IndestructibleEffect.registerCallback;
import static com.dreamtea.tag.IndestructibleTag.addDamageHookToAllItems;

public class TrulyUnbreakable implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static final String NAMESPACE = "truly-unbreakable";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		registerCallback();
		LOGGER.info("Hello Fabric world!");
		addDamageHookToAllItems();
	}
}
