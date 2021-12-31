package com.xtex.openlake.archive.entity.large_lake;

import com.xtex.openlake.OpenLake;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LargeLakeEntity extends PathAwareEntity {

    public static final Identifier ID = OpenLake.id("large_lake");
    public static final EntityType<LargeLakeEntity> TYPE = Registry.register(Registry.ENTITY_TYPE, ID,
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, LargeLakeEntity::new)
                    .dimensions(EntityDimensions.changing(3.7f, 8f))
                    .fireImmune()
                    .trackRangeChunks(10)
                    .build());
    public static final Identifier TEXTURE = OpenLake.id("skin/v5.png");
    @Nullable
    public ServerBossBar serverBossBar = null;

    protected LargeLakeEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        if (!world.isClient) {
            serverBossBar = new ServerBossBar(getName(), BossBar.Color.BLUE, BossBar.Style.PROGRESS);
            serverBossBar.setDarkenSky(true);
            serverBossBar.setThickenFog(true);
        }
    }

    public static void init() {
        //noinspection ConstantConditions
        FabricDefaultAttributeRegistry.register(TYPE, createMobAttributes());
    }

    public static void initClient() {
        EntityRendererRegistry.register(TYPE, LargeLakeEntityRenderer::new);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.29f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.8f)
                .add(EntityAttributes.GENERIC_ARMOR, 0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 250);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new AttackGoal(this));
        this.goalSelector.add(0, new GoToWalkTargetGoal(this, getMovementSpeed()));
        this.goalSelector.add(0, new LookAtEntityGoal(this, PlayerEntity.class, 200.0f));
        this.goalSelector.add(3, new EscapeDangerGoal(this, 1.25f));
        this.goalSelector.add(3, new SwimGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, false, true));
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0f;
    }

    @Override
    public void checkDespawn() {
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (getTarget() != null && source.getSource() != getTarget())
            amount *= 0.3f;
        if (!(source.isOutOfWorld() || source.isExplosive() || source.isMagic() || source.isUnblockable()) && getTarget() == null)
            return false; // Damage block
        return super.damage(source, amount);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        if (target instanceof ServerPlayerEntity && serverBossBar != null) {
            serverBossBar.clearPlayers();
            serverBossBar.addPlayer((ServerPlayerEntity) target);
        }
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (serverBossBar != null)
            serverBossBar.setPercent(getHealth() / getMaxHealth());
    }

    @Override
    public void setRemoved(RemovalReason reason) {
        super.setRemoved(reason);
        if (serverBossBar != null)
            serverBossBar.clearPlayers();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(getHealth() / getMaxHealth());
    }

}
