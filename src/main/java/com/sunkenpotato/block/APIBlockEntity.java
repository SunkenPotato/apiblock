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
import org.jetbrains.annotations.Nullable;
import org.apache.hc.core5.http.Header;

public class APIBlockEntity extends BlockEntity {

    private int tickCounter = 0;
    public final APIUpdater apiUpdater = new APIUpdater("");

    public APIBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.API_BLOCK_ENTITY, pos, state);
        apiUpdater.executeRequest();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        // set HTTP location
        nbt.putString("httpLoc", apiUpdater.getHttpLocation());

        // Set request headers
        NbtCompound locHeaders = new NbtCompound();
        for (Header entry : apiUpdater.getHeaders()) {
            locHeaders.putString(entry.getName(), entry.getValue());
        }

        nbt.put("headers", locHeaders);

        // set tick space
        nbt.putInt("tickSpace", apiUpdater.getTickSpace());

        super.writeNbt(nbt, wrapper);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        super.readNbt(nbt, wrapper);

        // read http location
        apiUpdater.setHttpLocation(nbt.getString("httpLoc"));

        // read headers
        NbtCompound nbtHeaders = nbt.getCompound("headers");

        for (String i : nbtHeaders.getKeys()) {
            apiUpdater.addHeader(i, nbtHeaders.getString(i));
        }

        // read tick space
        apiUpdater.setTickSpace(nbt.getInt("tickSpace"));
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
        if (blockEntity.tickCounter == blockEntity.apiUpdater.getTickSpace()) {

            if (!blockEntity.apiUpdater.getHttpLocation().isBlank())
                blockEntity.apiUpdater.executeRequest();

            boolean currentState = state.get(Properties.POWERED);
            boolean shouldEmit = !blockEntity.apiUpdater.getHttpLocation().isBlank() && blockEntity.apiUpdater.isSuccess();

            if (currentState != shouldEmit) {
                world.setBlockState(pos, state.with(Properties.POWERED, shouldEmit), 3);
                world.updateNeighborsAlways(pos, state.getBlock());
            }
            blockEntity.tickCounter = 0;
        }
    }
}
