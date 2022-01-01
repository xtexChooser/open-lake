package com.xtex.openlake.archive.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xtex.openlake.OpenLake;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static com.xtex.openlake.OpenLake.id;

public class RetreatSwordItem extends SwordItem {

    public static final Identifier IDENTIFIER = id("retreat_sword");
    public static final RetreatSwordItem ITEM = new RetreatSwordItem();

    @Environment(EnvType.CLIENT)
    public static boolean RENDER_STATE = false;

    public static void init() {
        Registry.register(Registry.ITEM, IDENTIFIER, ITEM);
    }

    public static void initClient() {
        HudRenderCallback.EVENT.register(RetreatSwordItem::onHudRender);
    }

    public RetreatSwordItem() {
        super(ToolMaterials.NETHERITE, 3, -2F, new FabricItemSettings()
                .group(OpenLake.ITEM_GROUP)
                .rarity(Rarity.UNCOMMON)
                .fireproof());
    }

    @Environment(EnvType.CLIENT)
    public static void onHudRender(MatrixStack matrixStack, float tickDelta) {
        assert MinecraftClient.getInstance().player != null;
        for (Hand hand : Hand.values()) {
            if (MinecraftClient.getInstance().player.getStackInHand(hand).getItem() == ITEM) {
                matrixStack.push();
                matrixStack.scale(1.5f, 1.5f, 1.5f);
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
                var width = MinecraftClient.getInstance().getWindow().getScaledWidth() / 9 / 3 * 2 + 2;
                var height = (MinecraftClient.getInstance().getWindow().getScaledHeight() - 40) / 9 / 3 * 2;
                RENDER_STATE = !RENDER_STATE;
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        MinecraftClient.getInstance().inGameHud.drawHeart(matrixStack, InGameHud.HeartType.NORMAL,
                                x * 9, y * 9, 0, RENDER_STATE, !RENDER_STATE);
                    }
                }
                matrixStack.pop();
                return;
            }
        }
    }

}
