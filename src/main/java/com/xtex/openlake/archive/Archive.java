package com.xtex.openlake.archive;

import com.xtex.openlake.OpenLake;
import com.xtex.openlake.archive.block.ArchiveWorldGateBlock;
import com.xtex.openlake.archive.dimension.ArchiveBiomeSource;
import com.xtex.openlake.archive.dimension.ArchiveChunkGenerator;
import com.xtex.openlake.archive.entity.large_lake.LargeLakeEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class Archive {

    public static final Identifier DIMENSION_IDENTIFIER = OpenLake.id("archive");
    public static final Identifier DIMENSION_TYPE_IDENTIFIER = OpenLake.id("archive");
    public static final RegistryKey<World> WORLD_REGISTRY_KEY = RegistryKey.of(Registry.WORLD_KEY, DIMENSION_IDENTIFIER);

    public static void init() {
        // Dimension
        ArchiveBiomeSource.init();
        ArchiveChunkGenerator.init();
        // Block
        ArchiveWorldGateBlock.init();
        // Entity
        LargeLakeEntity.init();
    }

    public static void initClient() {
        // Entity
        LargeLakeEntity.initClient();
    }

    public static boolean isArchiveDimension(ServerWorld world) {
        return DIMENSION_TYPE_IDENTIFIER.equals(world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getId(world.getDimension()));
    }

}
