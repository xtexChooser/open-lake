package com.xtex.openlake.archive.effect;

import com.xtex.openlake.OpenLake;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LowKeyEffect extends StatusEffect {

    public static final Identifier IDENTIFIER = OpenLake.id("low_key");
    public static final LowKeyEffect EFFECT = new LowKeyEffect();

    public static void init() {
        Registry.register(Registry.STATUS_EFFECT, IDENTIFIER, EFFECT);
    }

    protected LowKeyEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x2a6ab1);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }

}
