package net.arsenal.client;

import net.arsenal.client.particle.AbsorbParticleSpawner;
import net.arsenal.spell.ArsenalEffects;
import net.arsenal.spell.ArsenalProjectiles;
import net.arsenal.spell.ArsenalSpells;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.render.BuffParticleSpawner;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.api.render.StunParticleSpawner;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder.Batches;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.fx.SpellEngineParticles;

public class ArsenalClientMod {
    public static void init() {
        for (var entry: ArsenalSpells.all) {
            if (entry.mutator() != null) {
                SpellTooltip.addDescriptionMutator(entry.id(), entry.mutator());
            }
        }

        CustomParticleStatusEffect.register(
                ArsenalEffects.STUN.effect,
                new StunParticleSpawner()
        );

        CustomParticleStatusEffect.register(
                ArsenalEffects.FROSTBITE.effect,
                new StunParticleSpawner(SpellEngineParticles.snowflake.id())
        );

        final var guardingParticles = ParticleGroupBuilder
                .magic(SpellEngineParticles.magic_spark, ParticleGroup.Motion.FLOAT, ArsenalSpells.GUARDING_COLOR)
                .batch(b -> b.shape(ParticleGroup.Shape.PIPE).count(1F)
                        .speed(0.1F, 0.15F).verticalOrigin(Batches.FEET));
        CustomParticleStatusEffect.register(
                ArsenalEffects.GUARDING.effect,
                new BuffParticleSpawner(guardingParticles)
        );

        final var sunderingParticles = ParticleGroupBuilder
                .magic(SpellEngineParticles.magic_spark, ParticleGroup.Motion.BURST, ArsenalSpells.SUNDERING_COLOR)
                .batch(b -> b.shape(ParticleGroup.Shape.SPHERE).count(2F).speed(0.6F, 0.7F));
        CustomParticleStatusEffect.register(
                ArsenalEffects.SUNDERING.effect,
                new BuffParticleSpawner(sunderingParticles)
        );

        final var rampagingParticles = stripeColumn(ArsenalSpells.RAMPAGING_COLOR);
        CustomParticleStatusEffect.register(
                ArsenalEffects.RAMPAGING.effect,
                new BuffParticleSpawner(rampagingParticles)
                        .withGroundEffect(
                                SpellEngineParticles.area_effect_741.id().toString(),
                                ArsenalSpells.RAMPAGING_COLOR,
                                SpellEngineParticles.area_effect_741.texture().frames())
        );

        final var focusingParticles = stripeColumn(ArsenalSpells.FOCUSING_COLOR);
        CustomParticleStatusEffect.register(
                ArsenalEffects.FOCUSING.effect,
                new BuffParticleSpawner(focusingParticles)
                        .withGroundEffect(
                                SpellEngineParticles.area_effect_741.id().toString(),
                                ArsenalSpells.FOCUSING_COLOR,
                                SpellEngineParticles.area_effect_741.texture().frames())
        );

        final var surgingParticles = stripeColumn(ArsenalSpells.SURGING_COLOR);
        CustomParticleStatusEffect.register(
                ArsenalEffects.SURGING.effect,
                new BuffParticleSpawner(surgingParticles)
                        .withGroundEffect(
                                SpellEngineParticles.area_effect_741.id().toString(),
                                ArsenalSpells.SURGING_COLOR,
                                SpellEngineParticles.area_effect_741.texture().frames())
        );

        final var unyieldingParticles = stripeColumn(ArsenalSpells.UNYIELDING_COLOR);
        CustomParticleStatusEffect.register(
                ArsenalEffects.UNYIELDING.effect,
                new BuffParticleSpawner(unyieldingParticles)
                        .withGroundEffect(
                                SpellEngineParticles.area_effect_741.id().toString(),
                                ArsenalSpells.UNYIELDING_COLOR,
                                SpellEngineParticles.area_effect_741.texture().frames())
        );

        CustomParticleStatusEffect.register(
                ArsenalEffects.ABSORPTION.effect,
                new AbsorbParticleSpawner()
        );
    }

    /// The drifting stripe column shared by the four "on a roll" buffs, tinted per effect.
    private static ParticleGroup stripeColumn(net.spell_engine.client.util.Color color) {
        return ParticleGroupBuilder
                .magic(SpellEngineParticles.magic_stripe, ParticleGroup.Motion.FLOAT, color)
                .batch(b -> b.shape(ParticleGroup.Shape.PIPE).count(0.4F)
                        .speed(0.1F, 0.15F).verticalOrigin(Batches.FEET));
    }
}
