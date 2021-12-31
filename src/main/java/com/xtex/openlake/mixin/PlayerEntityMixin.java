package com.xtex.openlake.mixin;

import com.xtex.openlake.archive.block.ArchiveWorldGateBlock;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "createPlayerAttributes", at = @At("TAIL"))
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue().add(ArchiveWorldGateBlock.ATTRIBUTE_TELEPORT_X)
                .add(ArchiveWorldGateBlock.ATTRIBUTE_TELEPORT_Y)
                .add(ArchiveWorldGateBlock.ATTRIBUTE_TELEPORT_Z);
    }

}
