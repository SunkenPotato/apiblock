package com.sunkenpotato.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;

import static com.sunkenpotato.APIBlock.MOD_ID;

public class CommandRegistry {

    public void initialize() {
        apiSettingsCommand();
        argumentType();
    }

    private void argumentType() {
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "method"), MethodArgumentType.class, ConstantArgumentSerializer.of(MethodArgumentType::method));
    }

    private void apiSettingsCommand() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("apiblock")
                .executes(APISettingsCommand::noArgumentBase)
                .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                        .executes(APISettingsCommand::getBlockInfo)
                        .then(CommandManager.literal("set")
                                .then(CommandManager.literal("route")
                                        .then(CommandManager.argument("input", StringArgumentType.string()).executes(APISettingsCommand::setHTTPLocationSubcommand)))
                                .then(CommandManager.literal("tick")
                                        .then(CommandManager.argument("input", IntegerArgumentType.integer()).executes(APISettingsCommand::setTickSubcommand)))
                                .then(CommandManager.literal("method")
                                        .then(CommandManager.argument("input", MethodArgumentType.method()).executes(APISettingsCommand::setMethodSubcommand)))
                                .then(CommandManager.literal("enabled")
                                        .then(CommandManager.argument("input", BoolArgumentType.bool()).executes(APISettingsCommand::setEnabledSubcommand)))
                        )
                        .then(CommandManager.literal("reset")
                                .then(CommandManager.literal("tick").executes(APISettingsCommand::resetTickSubcommand))
                                .then(CommandManager.literal("header").executes(APISettingsCommand::resetHeaderSubcommand))
                                .then(CommandManager.literal("method").executes(APISettingsCommand::resetMethodSubcommand))
                        )
                        .then(CommandManager.literal("get")
                                .then(CommandManager.literal("route").executes(APISettingsCommand::getHTTPLocationSubcommand))
                                .then(CommandManager.literal("tick").executes(APISettingsCommand::getTickSubcommand))
                                .then(CommandManager.literal("header").executes(APISettingsCommand::getHeaderSubcommand))
                                .then(CommandManager.literal("method").executes(APISettingsCommand::getMethodSubcommand))
                        )
                        .then(CommandManager.literal("add")
                                .then(CommandManager.literal("tick")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer()).executes(APISettingsCommand::addTickSubcommand)))
                                .then(CommandManager.literal("header")
                                        .then(CommandManager.argument("name", StringArgumentType.string())
                                                .then(CommandManager.argument("value", StringArgumentType.string()).executes(APISettingsCommand::addHeaderSubcommand))))
                        )
                ))));
    }

}
