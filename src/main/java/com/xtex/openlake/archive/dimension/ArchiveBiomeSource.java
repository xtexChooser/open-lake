package com.xtex.openlake.archive.dimension;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xtex.openlake.OpenLake;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class ArchiveBiomeSource extends BiomeSource {

    public static final Identifier IDENTIFIER = OpenLake.id("archive");
    public static final Codec<ArchiveBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(RegistryLookupCodec.of(Registry.BIOME_KEY)
                                    .forGetter(it -> it.biomeRegistry),
                            Codec.LONG.fieldOf("seed")
                                    .stable()
                                    .forGetter(it -> it.seed))
                    .apply(instance, instance.stable(ArchiveBiomeSource::new)));

    public static void init() {
        Registry.register(Registry.BIOME_SOURCE, IDENTIFIER, CODEC);
    }

    public final long seed;
    public final SimplexNoiseSampler noise;
    public final Registry<Biome> biomeRegistry;
    public final Biome noneBiome;
    public final Biome centerLandBiome;
    public final Biome mainStarZoneBiome;

    protected ArchiveBiomeSource(Registry<Biome> biomeRegistry, long seed) {
        this(biomeRegistry, seed, biomeRegistry.getOrThrow(ArchiveBiomes.NONE_REGISTRY_KEY),
                biomeRegistry.getOrThrow(ArchiveBiomes.CENTER_LAND_REGISTRY_KEY),
                biomeRegistry.getOrThrow(ArchiveBiomes.MAIN_STAR_ZONE_REGISTRY_KEY));
    }

    protected ArchiveBiomeSource(Registry<Biome> biomeRegistry, long seed, Biome noneBiome, Biome centerLandBiome, Biome mainStarZoneBiome) {
        super(ImmutableList.of(noneBiome, centerLandBiome, mainStarZoneBiome));
        this.biomeRegistry = biomeRegistry;
        this.seed = seed;
        this.noise = new SimplexNoiseSampler(new ChunkRandom(new AtomicSimpleRandom(seed >> 3)));
        this.noneBiome = noneBiome;
        this.centerLandBiome = centerLandBiome;
        this.mainStarZoneBiome = mainStarZoneBiome;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new ArchiveBiomeSource(biomeRegistry, seed);
    }

    @Override
    public Biome getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        return getBlockBiome(BiomeCoords.toBlock(x), BiomeCoords.toBlock(z));
    }

    public Biome getBlockBiome(int x, int z) {
        return getBlockBiome(new Vec2f(x, z));
    }

    public Biome getBlockBiome(Vec2f pos) {
        var distance = pos.distanceSquared(Vec2f.ZERO);
        if (distance < 100L * 100L) {
            return centerLandBiome;
        } else if (distance > 500L * 500L) {
            return mainStarZoneBiome;
        } else {
            return noneBiome;
        }
    }

}
