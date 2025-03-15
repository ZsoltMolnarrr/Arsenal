package net.arsenal.client;

import net.arsenal.spell.ArsenalEffects;
import net.fabricmc.api.ClientModInitializer;
import net.arsenal.spell.ArsenalSpells;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.render.BuffParticleSpawner;
import net.spell_engine.api.render.StunParticleSpawner;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.fx.SpellEngineParticles;

public class ArsenalClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for (var entry: ArsenalSpells.entries) {
            if (entry.mutator() != null) {
                SpellTooltip.addDescriptionMutator(entry.id(), entry.mutator());
            }
        }

        CustomParticleStatusEffect.register(
                ArsenalEffects.STUN.effect,
                new StunParticleSpawner()
        );

        CustomParticleStatusEffect.register(
                ArsenalEffects.SLOWING.effect,
                new StunParticleSpawner(SpellEngineParticles.snowflake.id())
        );

//        final var witherPartciles = new ParticleBatch("infested",
//                ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
//                0.5F, 0.1F, 0.15F);
//        CustomParticleStatusEffect.register(
//                StatusEffects.WITHER.value(),
//                new BuffParticleSpawner(new ParticleBatch[]{ witherPartciles })
//        );
    }
}
