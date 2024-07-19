package com.sunkenpotato;

import com.mojang.brigadier.CommandDispatcher;
import com.sunkenpotato.block.BlockRegistry;
import com.sunkenpotato.command.CommandRegistry;
import net.fabricmc.api.ModInitializer;

import net.minecraft.block.RedstoneBlock;
import net.minecraft.block.RedstoneTorchBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIBlock implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("apiblock");
	public static final String MOD_ID = "apiblock";
	public static final BlockRegistry blockRegistry = new BlockRegistry();
	public static final CommandRegistry commandRegistry = new CommandRegistry();

	@Override
	public void onInitialize() {
		// This code runs as soo
		//		LOGGER.info("Hello Fabric world!");
		//	}
		//}n as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		blockRegistry.initialize();
		commandRegistry.initialize();
	}
}