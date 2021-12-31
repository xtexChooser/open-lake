package com.xtex.openlakejason.archive.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;

import static com.xtex.openlakejason.OpenLake.id;

public class ArchiveDimension {

    public static final Identifier BIOME_SOURCE_IDENTIFIER = id("archive");
    public static final Codec<ArchiveBiomeSource> BIOME_SOURCE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(RegistryLookupCodec.of(Registry.BIOME_KEY)
                                    .forGetter(it -> it.biomeRegistry),
                            Codec.LONG.fieldOf("seed")
                                    .stable()
                                    .forGetter(it -> it.seed))
                    .apply(instance, instance.stable(ArchiveBiomeSource::new)));

    public static final Identifier CHUNK_GENERATOR_IDENTIFIER = id("archive");
    public static final Codec<ArchiveChunkGenerator> CHUNK_GENERATOR_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ArchiveBiomeSource.CODEC.fieldOf("biome_source")
                                    .forGetter(it -> (ArchiveBiomeSource) it.getBiomeSource()),
                            StructuresConfig.CODEC.fieldOf("structures")
                                    .forGetter(ChunkGenerator::getStructuresConfig),
                            Codec.LONG.fieldOf("seed")
                                    .stable()
                                    .forGetter(it -> it.seed))
                    .apply(instance, instance.stable(ArchiveChunkGenerator::new)));

    public static final Identifier DIMENSION_IDENTIFIER = id("archive");
    public static final Identifier DIMENSION_TYPE_IDENTIFIER = id("archive");
    public static final RegistryKey<World> WORLD_REGISTRY_KEY = RegistryKey.of(Registry.WORLD_KEY, DIMENSION_IDENTIFIER);

    public static final Identifier BIOME_NONE_IDENTIFIER = id("archive_none");
    public static final RegistryKey<Biome> BIOME_NONE_REGISTRY_KEY = RegistryKey.of(Registry.BIOME_KEY, BIOME_NONE_IDENTIFIER);

    public static final Identifier BIOME_CENTER_LAND_IDENTIFIER = id("archive_center_land");
    public static final RegistryKey<Biome> BIOME_CENTER_LAND_REGISTRY_KEY = RegistryKey.of(Registry.BIOME_KEY, BIOME_CENTER_LAND_IDENTIFIER);

    public static final Identifier BIOME_MAIN_STAR_ZONE_IDENTIFIER = id("archive_main_star_zone");
    public static final RegistryKey<Biome> BIOME_MAIN_STAR_ZONE_REGISTRY_KEY = RegistryKey.of(Registry.BIOME_KEY, BIOME_MAIN_STAR_ZONE_IDENTIFIER);

    public static void init() {
        Registry.register(Registry.BIOME_SOURCE, BIOME_SOURCE_IDENTIFIER, BIOME_SOURCE_CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, CHUNK_GENERATOR_IDENTIFIER, CHUNK_GENERATOR_CODEC);
    }

    public static boolean isArchiveDimension(ServerWorld world) {
        return DIMENSION_TYPE_IDENTIFIER.equals(world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getId(world.getDimension()));
    }

}
