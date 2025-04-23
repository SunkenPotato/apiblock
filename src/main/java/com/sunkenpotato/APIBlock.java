package com.sunkenpotato;

import com.sunkenpotato.block.BlockRegistry;
import com.sunkenpotato.command.CommandRegistry;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIBlock implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "apiblock";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final BlockRegistry blockRegistry = new BlockRegistry();
	public static final CommandRegistry commandRegistry = new CommandRegistry();

	@Override
	public void onInitialize() {
		blockRegistry.initialize();
		commandRegistry.initialize();
	}
}