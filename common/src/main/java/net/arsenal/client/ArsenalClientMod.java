package net.arsenal.client;

import net.arsenal.client.particle.AbsorbParticleSpawner;
import net.arsenal.spell.ArsenalEffects;
import net.arsenal.spell.ArsenalProjectiles;
import net.arsenal.spell.ArsenalSpells;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.render.BuffParticleSpawner;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.api.render.StunParticleSpawner;
import net.spell_engine.api.spell.fx.ParticleBatch;
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

        final var guardingParticles = new ParticleBatch(
                SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
                ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
                1F, 0.1F, 0.15F)
                .color(ArsenalSpells.GUARDING_COLOR.toRGBA());
        CustomParticleStatusEffect.register(
                ArsenalEffects.GUARDING.effect,
                new BuffParticleSpawner(new ParticleBatch[]{ guardingParticles })
        );

        final var sunderingParticles = new ParticleBatch(
                SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.BURST).id().toString(),
                ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                2F, 0.6F, 0.7F)
                .color(ArsenalSpells.SUNDERING_COLOR.toRGBA());
        CustomParticleStatusEffect.register(
                ArsenalEffects.SUNDERING.effect,
                new BuffParticleSpawner(new ParticleBatch[]{ sunderingParticles })
        );

        final var rampagingParticles = new ParticleBatch(
                SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.STRIPE,
                        SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
                ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
                0.4F, 0.1F, 0.15F)
                .color(ArsenalSpells.RAMPAGING_COLOR.toRGBA());
        CustomParticleStatusEffect.register(
                ArsenalEffects.RAMPAGING.effect,
                new BuffParticleSpawner(new ParticleBatch[]{ rampagingParticles })
                        .withGroundEffect(
                                SpellEngineParticles.area_effect_741.id().toString(),
                                ArsenalSpells.RAMPAGING_COLOR,
                                SpellEngineParticles.area_effect_741.texture().frames())
        );

        final var focusingParticles = rampagingParticles.copy()
                .color(ArsenalSpells.FOCUSING_COLOR.toRGBA());
        CustomParticleStatusEffect.register(
                ArsenalEffects.FOCUSING.effect,
                new BuffParticleSpawner(new ParticleBatch[]{ focusingParticles })
                        .withGroundEffect(
                                SpellEngineParticles.area_effect_741.id().toString(),
                                ArsenalSpells.FOCUSING_COLOR,
                                SpellEngineParticles.area_effect_741.texture().frames())
        );

        final var surgingParticles = rampagingParticles.copy()
                .color(ArsenalSpells.SURGING_COLOR.toRGBA());
        CustomParticleStatusEffect.register(
                ArsenalEffects.SURGING.effect,
                new BuffParticleSpawner(new ParticleBatch[]{ surgingParticles })
                        .withGroundEffect(
                                SpellEngineParticles.area_effect_741.id().toString(),
                                ArsenalSpells.SURGING_COLOR,
                                SpellEngineParticles.area_effect_741.texture().frames())
        );

        final var unyieldingParticles = new ParticleBatch(
                SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.STRIPE,
                        SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
                ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
                0.4F, 0.1F, 0.15F)
                .color(ArsenalSpells.UNYIELDING_COLOR.toRGBA());
        CustomParticleStatusEffect.register(
                ArsenalEffects.UNYIELDING.effect,
                new BuffParticleSpawner(new ParticleBatch[]{ unyieldingParticles })
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
}
