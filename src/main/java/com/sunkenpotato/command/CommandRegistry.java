package com.sunkenpotato.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;

public class CommandRegistry {

    public void initialize() {
        apiSettingsCommand();
    }

    private void apiSettingsCommand() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {

            dispatcher.register(CommandManager.literal("apiblock")
                    .executes(APISettingsCommand::noArgumentBase)
                    .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                        .executes(APISettingsCommand::getBlockInfo)
                                    .then(CommandManager.literal("set")
                                            .then(CommandManager.literal("route")
                                                .then(CommandManager.argument("input", StringArgumentType.string()).executes(APISettingsCommand::setHTTPLocationSubcommand)))
                                            .then(CommandManager.literal("tick")
                                                .then(CommandManager.argument("input", IntegerArgumentType.integer()).executes(APISettingsCommand::setTickSubcommand)))
                                    )
                                    .then(CommandManager.literal("reset")
                                            .then(CommandManager.literal("route").executes(APISettingsCommand::resetRouteSubcommand))
                                            .then(CommandManager.literal("tick").executes(APISettingsCommand::resetTickSubcommand))
                                            .then(CommandManager.literal("header").executes(APISettingsCommand::resetHeaderSubcommand))
                                    )
                                    .then(CommandManager.literal("get")
                                            .then(CommandManager.literal("route").executes(APISettingsCommand::getHTTPLocationSubcommand))
                                            .then(CommandManager.literal("tick").executes(APISettingsCommand::getTickSubcommand))
                                            .then(CommandManager.literal("header").executes(APISettingsCommand::getHeaderSubcommand))
                                    )
                                    .then(CommandManager.literal("add")
                                            .then(CommandManager.literal("tick")
                                                    .then(CommandManager.argument("value", IntegerArgumentType.integer()).executes(APISettingsCommand::addTickSubcommand)))
                                            .then(CommandManager.literal("header")
                                                    .then(CommandManager.argument("name", StringArgumentType.string())
                                                            .then(CommandManager.argument("value", StringArgumentType.string()).executes(APISettingsCommand::addHeaderSubcommand))))
                                    )
                    ));

        }));
    }

}
