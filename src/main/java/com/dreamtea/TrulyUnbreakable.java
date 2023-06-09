package com.dreamtea;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dreamtea.tag.IndestructibleTag.addDamageHookToAllItems;

public class TrulyUnbreakable implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static final String NAMESPACE = "truly_unbreakable";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

	@Override
	public void onInitialize() {
		LOGGER.info("Unbreaking the game");
		addDamageHookToAllItems();
	}
}
