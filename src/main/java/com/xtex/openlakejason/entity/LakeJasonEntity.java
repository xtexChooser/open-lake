package com.xtex.openlakejason.entity;

import com.xtex.openlakejason.OpenLake;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class LakeJasonEntity extends PathAwareEntity {

    public static final Identifier ID = OpenLake.id("lakejason");
    public static final EntityType<LakeJasonEntity> TYPE = Registry.register(Registry.ENTITY_TYPE, ID,
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, LakeJasonEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
                    .fireImmune()
                    .build());
    public static final Identifier SPAWN_EGG_ID = OpenLake.id("lakejason_egg");
    public static final SpawnEggItem SPAWN_EGG_ITEM = Registry.register(Registry.ITEM, SPAWN_EGG_ID, new SpawnEggItem(
            LakeJasonEntity.TYPE, 0x5492e3, 0x0088fe,
            new Item.Settings()
                    .group(OpenLake.ITEM_GROUP)
                    .fireproof()));

    protected LakeJasonEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static void init() {
        //noinspection ConstantConditions
        FabricDefaultAttributeRegistry.register(TYPE, createMobAttributes());
    }

    public static void initClient() {
        EntityRendererRegistry.register(TYPE, LakeJasonEntityRenderer::new);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED)
                .add(EntityAttributes.GENERIC_ARMOR, 0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
        //this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal(this, 1.2, Ingredient.ofItems(Items.WATER_BUCKET), false));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 4.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        if (random.nextInt(1000) < 5) {
            playSound(SoundEvents.ENTITY_PARROT_AMBIENT, OpenLake.shouldEnableEasterEgg(1) ? 10.0f : 1.5f, 1.0f);
        }
    }

}
