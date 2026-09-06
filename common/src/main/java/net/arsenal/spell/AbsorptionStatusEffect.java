package net.arsenal.spell;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/// PORT (1.20.1): there is no `generic.max_absorption` attribute before 1.21, so this effect grants and
/// revokes the absorption points itself, exactly as vanilla 1.20.1's own `AbsorptionStatusEffect` does.
/// `onApplied` / `onRemoved` also take the extra `AttributeContainer` argument on this version.
public class AbsorptionStatusEffect extends StatusEffect {
    private final int healthPerStack;

    public AbsorptionStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
        this.healthPerStack = 2;
    }

    /// PORT (1.20.1): `applyUpdateEffect` returns `void` here, so the 1.21 trick of returning `false`
    /// once the absorption points are consumed (which self-cancels the effect) has no equivalent —
    /// the effect simply runs out its duration instead. `canApplyUpdateEffect` therefore returns false;
    /// everything this effect does happens in `onApplied` / `onRemoved`.
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), (float)(healthPerStack * (1 + amplifier))));
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.setAbsorptionAmount(Math.max(0F, entity.getAbsorptionAmount() - healthPerStack * (1 + amplifier)));
        super.onRemoved(entity, attributes, amplifier);
    }
}
