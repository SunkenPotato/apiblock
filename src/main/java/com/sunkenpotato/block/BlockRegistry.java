package com.sunkenpotato.block;

import com.sunkenpotato.util.RegistryUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockRegistry {

    public static final Block API_BLOCK = RegistryUtil.register(new APIBlock(AbstractBlock.Settings.create().solid()), "api_block", true);
    public static final BlockEntityType<APIBlockEntity> API_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(com.sunkenpotato.APIBlock.MOD_ID, "api_block_entity"),
                BlockEntityType.Builder.create(APIBlockEntity::new, API_BLOCK).build()
    );

    public void initialize() {

    }
}
