package com.xtex.openlakejason;

import com.xtex.openlakejason.archive.Archive;
import com.xtex.openlakejason.config.OpenLakeConfig;
import com.xtex.openlakejason.entity.LakeJasonEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public final class OpenLake implements ModInitializer, ClientModInitializer {

    public static final String MOD_ID = "open-lakejason";
    public static final Logger LOGGER = LoggerFactory.getLogger("OpenLake");

    public static final int TODAY_DAY = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    public static final boolean CAN_EASTER_EGG = (TODAY_DAY % 8) == 0;
    @Range(from = 0, to = 3)
    public static final int EASTER_EGG_SEED = (TODAY_DAY / 8) % 4;
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(id("item_group"))
            .icon(() -> new ItemStack(LakeJasonEntity.SPAWN_EGG_ITEM, shouldEnableEasterEgg(2) ? 18 : 1))
            .build();

    static {
        var mod = FabricLoader.getInstance().getModContainer(MOD_ID);
        if (mod.isPresent()) {
            LOGGER.info("Open-Lakejason v{}", mod.get().getMetadata().getVersion().getFriendlyString());
        } else {
            LOGGER.warn("Open-Lakejason works without '{}' mod id", MOD_ID);
        }
        LOGGER.info("Running in environment {} with mapping '{}'", FabricLoader.getInstance().getEnvironmentType(),
                FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace());
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.warn("Running in development environment");
        }
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static boolean shouldEnableEasterEgg(@Range(from = 0, to = 3) int seed) {
        return CAN_EASTER_EGG && EASTER_EGG_SEED == seed;
    }

    @Override
    public void onInitialize() {
        LakeJasonEntity.init();
        Archive.init();
    }

    @Override
    public void onInitializeClient() {
        OpenLakeConfig.initClient();
        LakeJasonEntity.initClient();
        Archive.initClient();
    }

}
