package com.sunkenpotato.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sunkenpotato.block.APIBlockEntity;
import com.sunkenpotato.block.BlockRegistry;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.apache.hc.core5.http.Header;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class APISettingsCommand {

    public static int noArgumentBase(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource()
                .sendFeedback(() -> Text.literal("Please supply coordinates"), false);

        return 1;
    }

    private enum APIBlockState {
        NOT_LOADED,
        NOT_AN_API_BLOCK,
        OK
    }

    private static APIBlockState getAPIBlockState(CommandContext<ServerCommandSource> ctx) {

        BlockPos pos;
        try {
            pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "pos");
        } catch (CommandSyntaxException ignored) {
            return APIBlockState.NOT_LOADED;
        }

        Optional<APIBlockEntity> requestedBlock = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY);

        if (requestedBlock.isPresent()) {
            return APIBlockState.OK;
        }

        return APIBlockState.NOT_AN_API_BLOCK;

    }

    public static int getBlockInfo(CommandContext<ServerCommandSource> ctx) {

        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found"), false);
                return 1;
            }
        }

        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();

        StringBuilder sb = new StringBuilder();
        sb.append("APIBlock at ").append(pos.getX()).append(", ").append(pos.getY()).append(", ").append(pos.getZ()).append(" has the following properties:\n");
        sb.append("HTTP Route: ").append(blockEntity.apiUpdater.httpLocation).append("\n");
        sb.append("Headers: \n");
        for (var i : blockEntity.apiUpdater.headers) {
            sb.append('\t').append(i.getName()).append(" : ").append(i.getValue()).append("\n");
        }

        sb.append("Update time: ").append(blockEntity.apiUpdater.tickSpace).append(" ticks.");

        ctx.getSource().sendFeedback(() -> Text.literal(sb.toString()), false);
        return 0;
    }

    public static int setHTTPLocationSubcommand(CommandContext<ServerCommandSource> ctx) {
        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found."), false);
                return 1;
            }
        }

        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();
        String input = StringArgumentType.getString(ctx, "input");

        blockEntity.apiUpdater.setURL(input);
        blockEntity.httpLoc = input;

        ctx.getSource().sendFeedback(() -> Text.literal("Set APIBlock HTTP location to: " + input), false);

        return 0;
    }

    public static int setTickSubcommand(CommandContext<ServerCommandSource> ctx) {
        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found."), false);
                return 1;
            }
        }

        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();
        int input = IntegerArgumentType.getInteger(ctx, "input");

        blockEntity.apiUpdater.setTickSpace(input);
        blockEntity.tickSpace = input;

        ctx.getSource().sendFeedback(() -> Text.literal("Set APIBlock tick space to: " + input), false);

        return 0;

    }

    public static int getHTTPLocationSubcommand(CommandContext<ServerCommandSource> ctx) {
        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found."), false);
                return 1;
            }
        }

        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();

        ctx.getSource().sendFeedback(() -> Text.literal("APIBlock has the following route: " + blockEntity.httpLoc), false);
        return 0;
    }

    public static int getTickSubcommand(CommandContext<ServerCommandSource> ctx) {
        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found."), false);
                return 1;
            }
        }
        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();

        ctx.getSource().sendFeedback(() -> Text.literal("APIBlock has the following tickSpace: " + blockEntity.tickSpace), false);
        return 0;
    }

    public static int getHeaderSubcommand(CommandContext<ServerCommandSource> ctx) {
        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found."), false);
                return 1;
            }
        }

        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();

        StringBuilder sb = new StringBuilder();
        sb.append("APIBlock has the following headers: \n");

        List<Header> headers = blockEntity.apiUpdater.headers;

        for (var i : headers) {
            sb.append(i.getName()).append(" : ").append(i.getValue()).append("\n");
        }

        ctx.getSource().sendFeedback(() -> Text.literal(sb.toString()), false);

        return 0;
    }

    public static int addTickSubcommand(CommandContext<ServerCommandSource> ctx) {
        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found."), false);
                return 1;
            }
        }

        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();
        int value = IntegerArgumentType.getInteger(ctx, "value");

        blockEntity.apiUpdater.setTickSpace(blockEntity.tickSpace + value);
        blockEntity.tickSpace += value;

        ctx.getSource().sendFeedback(() -> Text.literal("APIBlock tickSpeed is now at " + blockEntity.tickSpace), false);

        return 0;
    }

    public static int addHeaderSubcommand(CommandContext<ServerCommandSource> ctx) {
        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found."), false);
                return 1;
            }
        }
        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();

        String name = StringArgumentType.getString(ctx, "name");
        String value = StringArgumentType.getString(ctx, "value");

        blockEntity.apiUpdater.addHeader(name, value);

        ctx.getSource().sendFeedback(() -> Text.literal("Added the following header: \n" + name + " : " + value), false);

        return 0;
    }

    public static int resetRouteSubcommand(CommandContext<ServerCommandSource> ctx) {
        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found."), false);
                return 1;
            }
        }

        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();

        blockEntity.apiUpdater.setURL("");
        blockEntity.httpLoc = "";

        ctx.getSource().sendFeedback(() -> Text.literal("Reset route."), false);

        return 0;
    }

    public static int resetTickSubcommand(CommandContext<ServerCommandSource> ctx) {
        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found."), false);
                return 1;
            }
        }

        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();

        blockEntity.apiUpdater.setTickSpace(20);
        blockEntity.tickSpace = 20;

        ctx.getSource().sendFeedback(() -> Text.literal("Reset tick speed."), false);

        return 0;
    }

    public static int resetHeaderSubcommand(CommandContext<ServerCommandSource> ctx) {
        APIBlockState blockState = getAPIBlockState(ctx);
        switch (blockState) {
            case NOT_LOADED -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Position does not exist / not loaded."), false);
                return 1;
            }
            case NOT_AN_API_BLOCK -> {
                ctx.getSource().sendFeedback(() -> Text.literal("No block entity found."), false);
                return 1;
            }
        }

        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
        APIBlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(pos, BlockRegistry.API_BLOCK_ENTITY).get();

        blockEntity.apiUpdater.setHeaders(new ArrayList<>());

        ctx.getSource().sendFeedback(() -> Text.literal("Reset headers."), false);

        return 0;
    }


}

