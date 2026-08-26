package net.arsenal.spell;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AbsorptionStatusEffect extends MobEffect {
    private final int healthPerStack;

    public AbsorptionStatusEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.healthPerStack = 2;
    }

    // 1.21.2+: `applyUpdateEffect` gained a leading `ServerWorld` (it only runs server side now,
    // so the former `world.isClient` escape hatch is gone).
    @Override
    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {
        return entity.getAbsorptionAmount() > 0.0F;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        super.onEffectStarted(entity, amplifier);
        entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), (float)(healthPerStack * (1 + amplifier))));
    }
}
