package com.xtex.openlake.entity;

import com.xtex.openlake.OpenLake;
import com.xtex.openlake.config.OpenLakeConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LakeJasonEntityRenderer extends MobEntityRenderer<LakeJasonEntity, PlayerEntityModel<LakeJasonEntity>> {

    public LakeJasonEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PlayerEntityModel<>(context.getPart(OpenLakeConfig.get().useSlimModel ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), OpenLakeConfig.get().useSlimModel), 0.5f);
        var slim = OpenLakeConfig.get().useSlimModel;
        this.addFeature(new ArmorFeatureRenderer<>(this, new BipedEntityModel<>(context.getPart(
                slim ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)),
                new BipedEntityModel<>(context.getPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR
                        : EntityModelLayers.PLAYER_OUTER_ARMOR))));
        this.addFeature(new HeldItemFeatureRenderer<>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<>(context, this));
    }

    @Override
    public Identifier getTexture(LakeJasonEntity entity) {
        return OpenLake.id("skin/v5.png");
    }

}
