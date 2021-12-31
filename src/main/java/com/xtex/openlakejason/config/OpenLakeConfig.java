package com.xtex.openlakejason.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xtex.openlakejason.OpenLake;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Environment(EnvType.CLIENT)
public class OpenLakeConfig {

    private static final File FILE = FabricLoader.getInstance().getConfigDir().resolve("open_lakejason.json").toFile();
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    private static final OpenLakeConfig INSTANCE = load();
    public boolean useSlimModel = false;
    public boolean enableEasterEggs = true;

    public static OpenLakeConfig get() {
        return INSTANCE;
    }

    private static OpenLakeConfig load() {
        if (FILE.exists()) {
            try {
                return GSON.fromJson(FileUtils.readFileToString(FILE, StandardCharsets.UTF_8), OpenLakeConfig.class);
            } catch (IOException e) {
                OpenLake.LOGGER.error("Error loading exists configuration from " + FILE.getAbsolutePath(), e);
            }
        }
        return new OpenLakeConfig();
    }

    public static void write() {
        try {
            FileUtils.writeStringToFile(FILE, GSON.toJson(INSTANCE), StandardCharsets.UTF_8);
        } catch (IOException e) {
            OpenLake.LOGGER.error("Error writing configuration to " + FILE.getAbsolutePath(), e);
        }
    }

    public static void initClient() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("openlake_config")
                .executes((context) -> {
                    openConfigScreen();
                    return 0;
                }));
    }

    public static Screen createConfigScreen(Screen parent) {
        var builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("openlakejason.config"))
                .setSavingRunnable(OpenLakeConfig::write);
        builder.getOrCreateCategory(new TranslatableText("openlakejason.config.general"))
                .addEntry(builder.entryBuilder()
                        .startBooleanToggle(new TranslatableText("openlakejason.config.general.slim"), INSTANCE.useSlimModel)
                        .setDefaultValue(false)
                        .setSaveConsumer((value) -> INSTANCE.useSlimModel = value)
                        .requireRestart()
                        .build())
                .addEntry(builder.entryBuilder()
                        .startBooleanToggle(new TranslatableText("openlakejason.config.general.easter_eggs"), INSTANCE.enableEasterEggs)
                        .setDefaultValue(true)
                        .setSaveConsumer((value) -> INSTANCE.enableEasterEggs = value)
                        .requireRestart()
                        .build());
        builder.getOrCreateCategory(new TranslatableText("openlakejason.config.devtools"))
                .addEntry(builder.entryBuilder().startTextDescription(new LiteralText("Using mod id: " + OpenLake.MOD_ID)).build())
                .addEntry(builder.entryBuilder().startTextDescription(new LiteralText("Is mod with current mod id present: " + FabricLoader.getInstance().getModContainer(OpenLake.MOD_ID).isPresent())).build())
                .addEntry(builder.entryBuilder().startTextDescription(new LiteralText("Using environment type: " + FabricLoader.getInstance().getEnvironmentType())).build())
                .addEntry(builder.entryBuilder().startTextDescription(new LiteralText("Using namespace: " + FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace())).build())
                .addEntry(builder.entryBuilder().startTextDescription(new LiteralText("Is development environment: " + FabricLoader.getInstance().isDevelopmentEnvironment())).build())
                .addEntry(builder.entryBuilder().startTextDescription(new LiteralText("Game directory: " + FabricLoader.getInstance().getGameDir().toFile().getAbsolutePath())).build())
                .addEntry(builder.entryBuilder().startTextDescription(new LiteralText("Config directory: " + FabricLoader.getInstance().getConfigDir().toFile().getAbsolutePath())).build());
        return builder.build();
    }

    public static void openConfigScreen() {
        MinecraftClient.getInstance().setScreen(createConfigScreen(MinecraftClient.getInstance().currentScreen));
    }

}
