package com.xtex.openlakejason.archive.block;

import com.xtex.openlakejason.OpenLake;
import com.xtex.openlakejason.archive.dimension.ArchiveDimension;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;

import static com.xtex.openlakejason.OpenLake.id;

@SuppressWarnings("deprecation")
public class ArchiveWorldGateBlock extends Block {

    public static final Identifier IDENTIFIER = id("archive_world_gate");
    public static final ArchiveWorldGateBlock BLOCK = new ArchiveWorldGateBlock();

    public static final Identifier ATTRIBUTE_TELEPORT_X_IDENTIFIER = id("archive.teleport.x");
    public static final EntityAttribute ATTRIBUTE_TELEPORT_X = new ClampedEntityAttribute("atttribute.open-lakejason.archive_teleport_x", 0.0f, Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final Identifier ATTRIBUTE_TELEPORT_Y_IDENTIFIER = id("archive.teleport.y");
    public static final EntityAttribute ATTRIBUTE_TELEPORT_Y = new ClampedEntityAttribute("atttribute.open-lakejason.archive_teleport_y", 0.0f, Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final Identifier ATTRIBUTE_TELEPORT_Z_IDENTIFIER = id("archive.teleport.z");
    public static final EntityAttribute ATTRIBUTE_TELEPORT_Z = new ClampedEntityAttribute("atttribute.open-lakejason.archive_teleport_z", 0.0f, Integer.MIN_VALUE, Integer.MAX_VALUE);

    public static void init() {
        Registry.register(Registry.BLOCK, IDENTIFIER, BLOCK);
        Registry.register(Registry.ATTRIBUTE, ATTRIBUTE_TELEPORT_X_IDENTIFIER, ATTRIBUTE_TELEPORT_X);
        Registry.register(Registry.ATTRIBUTE, ATTRIBUTE_TELEPORT_Y_IDENTIFIER, ATTRIBUTE_TELEPORT_Y);
        Registry.register(Registry.ATTRIBUTE, ATTRIBUTE_TELEPORT_Z_IDENTIFIER, ATTRIBUTE_TELEPORT_Z);
    }

    public ArchiveWorldGateBlock() {
        super(FabricBlockSettings.of(Material.STONE, MapColor.BLACK)
                .strength(-1.0f, 0.0f)
                .requiresTool());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.world.isClient && checkPower((ServerWorld) world, pos)) {
            var isInArchive = ArchiveDimension.isArchiveDimension((ServerWorld) world);
            var server = Objects.requireNonNull(player.getServer());
            if (isInArchive) {
                if (!player.getAttributes().hasAttribute(ATTRIBUTE_TELEPORT_X) || !player.getAttributes().hasAttribute(ATTRIBUTE_TELEPORT_Y)
                        || !player.getAttributes().hasAttribute(ATTRIBUTE_TELEPORT_Y)) {
                    OpenLake.LOGGER.warn("Trying to teleport {} back to OverWorld but some teleport attributes missing", player.getName());
                } else {
                    ((ServerPlayerEntity) player).teleport(server.getOverworld(),
                            player.getAttributeValue(ATTRIBUTE_TELEPORT_X), player.getAttributeValue(ATTRIBUTE_TELEPORT_Y),
                            player.getAttributeValue(ATTRIBUTE_TELEPORT_Z), player.getYaw(), player.getPitch());
                }
            } else {
                Objects.requireNonNull(player.getAttributes().getCustomInstance(ATTRIBUTE_TELEPORT_X)).setBaseValue(player.getX());
                Objects.requireNonNull(player.getAttributes().getCustomInstance(ATTRIBUTE_TELEPORT_Y)).setBaseValue(player.getY());
                Objects.requireNonNull(player.getAttributes().getCustomInstance(ATTRIBUTE_TELEPORT_Z)).setBaseValue(player.getZ());
                ((ServerPlayerEntity) player).teleport(server.getWorld(ArchiveDimension.WORLD_REGISTRY_KEY), 0.5f, 43f, 0.5f,
                        player.getYaw() + (OpenLake.shouldEnableEasterEgg(3) ? 16 : 0), player.getPitch());
            }
        }
        return ActionResult.SUCCESS;
    }

    public static boolean checkPower(ServerWorld world, BlockPos pos) {
        if (ArchiveDimension.isArchiveDimension(world))
            return true;
        return true; // @TODO
    }

}
