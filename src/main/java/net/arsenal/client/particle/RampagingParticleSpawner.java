package net.arsenal.client.particle;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.render.BuffParticleSpawner;
import net.spell_engine.api.spell.fx.ParticleBatch;

import java.util.List;

public class RampagingParticleSpawner extends BuffParticleSpawner {
    public RampagingParticleSpawner(List<String> particleIds, float particleCount, float min_speed, float max_speed) {
        super(particleIds, particleCount, min_speed, max_speed);
    }

    public RampagingParticleSpawner(String particleId, float particleCount, float min_speed, float max_speed) {
        super(particleId, particleCount, min_speed, max_speed);
    }

    public RampagingParticleSpawner(String particleId, float particleCount) {
        super(particleId, particleCount);
    }

    public RampagingParticleSpawner(ParticleBatch[] particles) {
        super(particles);
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        super.spawnParticles(livingEntity, amplifier);
    }
}
