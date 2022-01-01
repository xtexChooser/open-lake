package com.xtex.openlake.archive.dimension;

import com.xtex.openlake.OpenLake;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class ArchiveBiomes {

    public static final Identifier NONE_IDENTIFIER = OpenLake.id("archive_none");
    public static final RegistryKey<Biome> NONE_REGISTRY_KEY = RegistryKey.of(Registry.BIOME_KEY, NONE_IDENTIFIER);

    public static final Identifier CENTER_LAND_IDENTIFIER = OpenLake.id("archive_center_land");
    public static final RegistryKey<Biome> CENTER_LAND_REGISTRY_KEY = RegistryKey.of(Registry.BIOME_KEY, CENTER_LAND_IDENTIFIER);

    public static final Identifier MAIN_STAR_ZONE_IDENTIFIER = OpenLake.id("archive_main_star_zone");
    public static final RegistryKey<Biome> MAIN_STAR_ZONE_REGISTRY_KEY = RegistryKey.of(Registry.BIOME_KEY, MAIN_STAR_ZONE_IDENTIFIER);

}