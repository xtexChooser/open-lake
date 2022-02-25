package com.xtex.openlake.archive.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xtex.openlake.OpenLake;
import com.xtex.openlake.archive.block.ArchiveWorldGateBlock;
import com.xtex.openlake.archive.entity.large_lake.LargeLakeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import org.spongepowered.noise.module.NoiseModule;
import org.spongepowered.noise.module.source.Spheres;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ArchiveChunkGenerator extends ChunkGenerator {

    public static final Identifier IDENTIFIER = OpenLake.id("archive");
    public static final Codec<ArchiveChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ArchiveBiomeSource.CODEC.fieldOf("biome_source")
                                    .forGetter(it -> (ArchiveBiomeSource) it.getBiomeSource()),
                            StructuresConfig.CODEC.fieldOf("structures")
                                    .forGetter(ChunkGenerator::getStructuresConfig),
                            Codec.LONG.fieldOf("seed")
                                    .stable()
                                    .forGetter(it -> it.seed))
                    .apply(instance, instance.stable(ArchiveChunkGenerator::new)));

    public static void init() {
        Registry.register(Registry.CHUNK_GENERATOR, IDENTIFIER, CODEC);
    }

    public final long seed;
    public final Biome noneBiome;
    public final Biome centerLandBiome;
    public final Biome mainStarZoneBiome;
    public final NoiseModule heightNoise;
    public final SimplexNoiseSampler bottomNoise;

    public ArchiveChunkGenerator(ArchiveBiomeSource biomeSource, StructuresConfig structuresConfig, long seed) {
        this(biomeSource, structuresConfig, seed, biomeSource.biomeRegistry.getOrThrow(ArchiveBiomes.NONE_REGISTRY_KEY),
                biomeSource.biomeRegistry.getOrThrow(ArchiveBiomes.CENTER_LAND_REGISTRY_KEY),
                biomeSource.biomeRegistry.getOrThrow(ArchiveBiomes.MAIN_STAR_ZONE_REGISTRY_KEY));
    }

    public ArchiveChunkGenerator(BiomeSource biomeSource, StructuresConfig structuresConfig, long seed, Biome noneBiome,
                                 Biome centerLandBiome, Biome mainStarZoneBiome) {
        super(biomeSource, structuresConfig);
        this.seed = seed;
        this.noneBiome = noneBiome;
        this.centerLandBiome = centerLandBiome;
        this.mainStarZoneBiome = mainStarZoneBiome;
        {
            var spheres = new Spheres();
            spheres.setFrequency(((float) (seed >> 7)) / 700);
            this.heightNoise = spheres;
        }
        this.bottomNoise = new SimplexNoiseSampler(new ChunkRandom(new Xoroshiro128PlusPlusRandom(seed)));
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new ArchiveChunkGenerator((ArchiveBiomeSource) biomeSource, getStructuresConfig(), seed);
    }

    @Override
    public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
        return (x, y, z) -> MultiNoiseUtil.createNoiseValuePoint(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep) {
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void populateEntities(ChunkRegion region) {
        var pos = region.getCenterPos();
        var random = new ChunkRandom(new Xoroshiro128PlusPlusRandom(seed));
        random.setPopulationSeed(region.getSeed(), pos.getStartX(), pos.getStartZ());
        SpawnHelper.populateEntities(region, region.getBiome(pos.getStartPos()), pos, random);
        if (region.getCenterPos().x == 0 && region.getCenterPos().z == 0) {
            OpenLake.LOGGER.info("Spawning large lake in {}", region.getCenterPos());
            var entity = new LargeLakeEntity(LargeLakeEntity.TYPE, region.toServerWorld());
            entity.setPosition(5, 50, 5);
            region.toServerWorld().spawnEntity(entity);
        }
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.supplyAsync(() -> {
            var blockPos = new BlockPos.Mutable();
            for (int x = 0; x < 16; x++) {
                blockPos.setX(x);
                for (int z = 0; z < 16; z++) {
                    blockPos.setZ(z);
                    var sample = getColumnSample(chunk.getPos().getOffsetX(x), chunk.getPos().getOffsetZ(z), chunk.getHeightLimitView());
                    for (int y = getMinimumY(); y < getWorldHeight(); y++) {
                        blockPos.setY(y);
                        chunk.setBlockState(blockPos, sample.getState(y), false);
                    }
                }
            }
            // Spawn point chunk
            if (chunk.getPos().x == 0 && chunk.getPos().z == 0) {
                OpenLake.LOGGER.info("Generating archive world gate block for center land");
                chunk.setBlockState(new BlockPos(0, 42, 0), ArchiveWorldGateBlock.BLOCK.getDefaultState(), false);
            }
            return chunk;
        }, executor);
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return 84;
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
        return getSurfaceHeight(new Vec2f(x, z));
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        var pos = new Vec2f(x, z);

        // Initialize blocks array
        var blocks = new BlockState[getWorldHeight()];
        for (int i = 0; i < blocks.length; i++)
            blocks[i] = Blocks.AIR.getDefaultState();

        // Get biome
        var biome = ((ArchiveBiomeSource) biomeSource).getBlockBiome(pos);
        if (biome != noneBiome) {
            // Get heights
            var surfaceHeight = getSurfaceHeight(pos, biome);
            var bottomHeight = getBottomHeight(surfaceHeight, pos);

            // Fill with stone
            for (int i = bottomHeight; i < surfaceHeight; i++)
                blocks[i] = Blocks.STONE.getDefaultState();

            // Generate grass layer for main star zone
            if (biome == mainStarZoneBiome) {
                for (int i = surfaceHeight; i > (surfaceHeight - 4); i--)
                    blocks[i] = Blocks.DIRT.getDefaultState();
                blocks[surfaceHeight] = Blocks.GRASS_BLOCK.getDefaultState();
            }
        }
        return new VerticalBlockSample(0, blocks);
    }

    public int getSurfaceHeight(Vec2f pos) {
        return getSurfaceHeight(pos, ((ArchiveBiomeSource) biomeSource).getBlockBiome(pos));
    }

    public int getSurfaceHeight(Vec2f pos, Biome biome) {
        if (biome == centerLandBiome) {
            return getCenterLandSurfaceHeight(pos);
        } else if (biome == mainStarZoneBiome) {
            return getMainStarZoneSurfaceHeight(pos);
        } else {
            return 0;
        }
    }

    public int getCenterLandSurfaceHeight(Vec2f pos) {
        var distance = pos.distanceSquared(Vec2f.ZERO);
        if (distance < 65 * 65) { // Center
            return (int) (getSeaLevel() * (0.5f + 0.4f * (distance / (65 * 65))));
        } else { // Border
            if (((int) distance & 0b1) == 0) {
                return (int) (getSeaLevel() * heightNoise.get(pos.x, pos.y, 2f));
            } else {
                return 0;
            }
        }
    }

    public int getMainStarZoneSurfaceHeight(Vec2f pos) {
        var areaX = (int) pos.x >> 5;
        var areaZ = (int) pos.y >> 5;
        return (getMainStarZoneSurfaceBaseHeight(areaX, areaZ)
                + getMainStarZoneSurfaceBaseHeight(areaX - 1, areaX - 1)
                + getMainStarZoneSurfaceBaseHeight(areaX - 1, areaX + 1)
                + getMainStarZoneSurfaceBaseHeight(areaX + 1, areaX - 1)
                + getMainStarZoneSurfaceBaseHeight(areaX + 1, areaX + 1)) / 4;
    }

    public int getMainStarZoneSurfaceBaseHeight(int areaX, int areaZ) {
        return (int) (getSeaLevel() * (0.9f + 0.2 * heightNoise.get(areaX, seed & 0b1111, areaZ)));
    }

    public int getBottomHeight(int surfaceHeight, Vec2f pos) {
        return (int) (surfaceHeight * Math.abs(bottomNoise.sample(pos.x, pos.y)) * Math.abs(bottomNoise.sample(pos.x * 2, pos.y * 2)));
    }

}
