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
import org.apache.hc.core5.http.message.BasicHeader;
import org.jetbrains.annotations.Nullable;
import org.apache.hc.core5.http.Header;


import java.util.ArrayList;
import java.util.List;

public class APIBlockEntity extends BlockEntity {
    public String httpLoc = "";
    private final List<Header> headers = new ArrayList<>();
    public int tickSpace = 20;
    private int tickCounter = 0;
    public final APIUpdater apiUpdater = new APIUpdater(httpLoc);

    public APIBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.API_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        // set HTTP location
        nbt.putString("httpLoc", httpLoc);

        apiUpdater.setURL(httpLoc);

        // Set request headers
        NbtCompound locHeaders = new NbtCompound();
        for (Header entry : headers) {
            locHeaders.putString(entry.getName(), entry.getValue());
        }

        nbt.put("headers", locHeaders);

        apiUpdater.setHeaders(headers);
        // set tick space
        nbt.putInt("tickSpace", tickSpace);

        super.writeNbt(nbt, wrapper);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        super.readNbt(nbt, wrapper);

        // read http location
        httpLoc = nbt.getString("httpLoc");

        // read headers
        NbtCompound nbtHeaders = nbt.getCompound("headers");
        headers.clear();

        for (String i : nbtHeaders.getKeys()) {
            headers.add(new BasicHeader(i, nbtHeaders.getString(i)));
        }

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

    public static void tick(World world, BlockPos pos, BlockState state, APIBlockEntity blockEntity) {
        blockEntity.tickCounter++;
        if (blockEntity.tickCounter == blockEntity.tickSpace) {
            blockEntity.apiUpdater.executeRequest();

            boolean currentState = state.get(Properties.POWERED);
            boolean shouldEmit = blockEntity.apiUpdater.isSuccess();

            if (currentState != shouldEmit) {
                world.setBlockState(pos, state.with(Properties.POWERED, shouldEmit), 3);
                world.updateNeighborsAlways(pos, state.getBlock());
            }
            blockEntity.tickCounter = 0;
        }
    }
}
