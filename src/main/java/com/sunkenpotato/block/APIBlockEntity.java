package com.sunkenpotato.block;

import com.sunkenpotato.updater.APIUpdater;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.hc.core5.http.Header;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.util.ArrayList;

import static com.sunkenpotato.APIBlock.LOGGER;

public class APIBlockEntity extends BlockEntity {
    public final APIUpdater apiUpdater = new APIUpdater("http://127.0.0.1");
    public int tickSpace = 20;
    public boolean enabled = false;
    private int tickCounter = 0;

    public APIBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.API_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, APIBlockEntity blockEntity) {
        if (!blockEntity.enabled) {
            if (state.get(Properties.POWERED))
                updateState(world, pos, state, false);

            return;
        }

        blockEntity.tickCounter++;
        if (blockEntity.tickCounter == blockEntity.tickSpace) {
            LOGGER.info("a");
            blockEntity.apiUpdater.executeRequest();

            boolean currentState = state.get(Properties.POWERED);
            boolean shouldEmit = blockEntity.apiUpdater.isSuccess();

            if (currentState != shouldEmit) {
                LOGGER.info("b");
                updateState(world, pos, state, shouldEmit);
            }
            blockEntity.tickCounter = 0;
        }
    }

    private static void updateState(World world, BlockPos pos, BlockState state, boolean shouldEmit) {
        world.setBlockState(pos, state.with(Properties.POWERED, shouldEmit), 3);
        world.updateNeighborsAlways(pos, state.getBlock());
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        // set HTTP location
        nbt.putString("httpLoc", apiUpdater.httpLocation.toString());

        // Set request headers
        NbtCompound locHeaders = new NbtCompound();
        for (Header entry : apiUpdater.getHeaders()) {
            locHeaders.putString(entry.getName(), entry.getValue());
        }

        nbt.put("headers", locHeaders);

        // set tick space
        nbt.putInt("tickSpace", tickSpace);

        nbt.putBoolean("enabled", enabled);

        super.writeNbt(nbt, wrapper);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        super.readNbt(nbt, wrapper);

        // read http location
        try {
            apiUpdater.setURI(nbt.getString("httpLoc"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // read headers
        NbtCompound nbtHeaders = nbt.getCompound("headers");
        apiUpdater.setHeaders(new ArrayList<>());

        for (String i : nbtHeaders.getKeys()) {
            apiUpdater.addHeader(i, nbtHeaders.getString(i));
        }

        enabled = nbt.getBoolean("enabled");

        // read tick space
        tickSpace = nbt.getInt("tickSpace");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup wrapperLookup) {
        return createNbt(wrapperLookup);
    }
}
