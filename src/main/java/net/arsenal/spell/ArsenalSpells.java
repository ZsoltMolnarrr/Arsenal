package net.arsenal.spell;

import net.arsenal.ArsenalMod;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
import net.spell_power.api.SpellSchools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArsenalSpells {
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator) {
    }

    public static final List<Entry> entries = new ArrayList<>();

    private static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static final float T1_USE_EFFECT_DURATION = 10;
    private static final float T1_PROC_EFFECT_DURATION = 6;
    private static final float T1_USE_EFFECT_COOLDOWN = 60;
    private static final float T1_PROC_EFFECT_COOLDOWN = 45;
    private static final float T1_PROC_CHANCE = 0.05F;

    private static final float T2_USE_EFFECT_DURATION = 10;
    private static final float T2_PROC_EFFECT_DURATION = 8;
    private static final float T2_USE_EFFECT_COOLDOWN = 60;
    private static final float T2_PROC_EFFECT_COOLDOWN = 45;
    private static final float T2_PROC_CHANCE = 0.06F;

    private static final float T3_TRANCE_CHANCE = 0.1F;
    private static final float T3_TRANCE_DURATION = 10F;
    private static final float T3_TRANCE_COOLDOWN = 45F;
    private static final float T3_PERK_CC_DURATION = 2;
    private static final float T3_PERK_CC_COOLDOWN = 20;

    private static final float T4_USE_EFFECT_DURATION = 15;
    private static final float T4_USE_EFFECT_COOLDOWN = 90;
    private static final float T4_AREA_RANGE = 15;
    private static final float T4_ZONE_RANGE = 3;

    private static Spell activeSpellBase() {
        var spell = new Spell();
        spell.range = 0;
        spell.tier = 8;

        spell.type = Spell.Type.ACTIVE;
        spell.active = new Spell.Active();

        spell.tooltip = new Spell.Tooltip();
        spell.tooltip.name = new Spell.Tooltip.LineOptions(false, false);
        spell.tooltip.description.color = Formatting.DARK_GREEN.asString();
        spell.tooltip.description.show_in_compact = true;

        return spell;
    }

    private static Spell passiveSpellBase() {
        var spell = new Spell();
        spell.range = 0;
        spell.tier = 8;

        spell.type = Spell.Type.PASSIVE;
        spell.passive = new Spell.Passive();

        spell.tooltip = new Spell.Tooltip();
        spell.tooltip.name = new Spell.Tooltip.LineOptions(false, false);
        spell.tooltip.description.color = Formatting.DARK_GREEN.asString();
        spell.tooltip.description.show_in_compact = true;

        return spell;
    }

    private static Spell.Impact createEffectImpact(String effectIdString, float duration) {
        var buff = new Spell.Impact();
        buff.action = new Spell.Impact.Action();
        buff.action.type = Spell.Impact.Action.Type.STATUS_EFFECT;
        buff.action.status_effect = new Spell.Impact.Action.StatusEffect();
        buff.action.status_effect.effect_id = effectIdString;
        buff.action.status_effect.duration = duration;
        return buff;
    }

    private static void configureCooldown(Spell spell, float duration) {
        if (spell.cost == null) {
            spell.cost = new Spell.Cost();
        }
        if (spell.cost.cooldown == null) {
            spell.cost.cooldown = new Spell.Cost.Cooldown();
        }
        spell.cost.cooldown.duration = duration;
    }

    private static @NotNull ParticleBatch lesserActivateParticles(SpellEngineParticles.MagicParticleFamily family, int count) {
        return lesserActivateParticles(SpellEngineParticles.getMagicParticleVariant(
                family,
                SpellEngineParticles.MagicParticleFamily.Shape.SPARK,
                SpellEngineParticles.MagicParticleFamily.Motion.DECELERATE).id().toString(),
                count);
    }

    private static @NotNull ParticleBatch lesserActivateParticles(String particleId, int count) {
        return new ParticleBatch(
                particleId,
                ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                count, 0.14F, 0.15F);
    }

    private static final Identifier HOLY_IMPACT_DECELERATE = SpellEngineParticles.getMagicParticleVariant(
            SpellEngineParticles.HOLY,
            SpellEngineParticles.MagicParticleFamily.Shape.IMPACT,
            SpellEngineParticles.MagicParticleFamily.Motion.DECELERATE
    ).id();

    public static Entry melee_radiance = add(melee_radiance());
    private static Entry melee_radiance() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "melee_radiance");
        var title = "Radiance";
        var description = "Chance on hit: Heals you and nearby allies by {heal}.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.HEALING;

        // spell.release.sound = new Sound(SpellEngineSounds.GENERIC_HEALING_IMPACT_1.id().toString());

        var trigger = new Spell.Trigger();
        trigger.chance = 0.5F;
        trigger.chance_batching = true;
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        spell.passive.triggers = List.of(trigger);

        var heal = new Spell.Impact();
        heal.attribute = EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString();
        heal.action = new Spell.Impact.Action();
        heal.action.type = Spell.Impact.Action.Type.HEAL;
        heal.action.heal = new Spell.Impact.Action.Heal();
        heal.action.heal.spell_power_coefficient = 0.5F;
        heal.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.getMagicParticleVariant(
                        SpellEngineParticles.HOLY,
                        SpellEngineParticles.MagicParticleFamily.Shape.SPARK,
                        SpellEngineParticles.MagicParticleFamily.Motion.DECELERATE).id().toString(),
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        20, 0.1F, 0.1F),
                new ParticleBatch(
                        HOLY_IMPACT_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.2F, 0.25F)
        };
        heal.sound = new Sound(SpellEngineSounds.GENERIC_HEALING_IMPACT_1.id().toString());

        spell.impacts = List.of(heal);
        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 2;

        configureCooldown(spell, 0.5F);
        spell.cost.cooldown.hosting_item = false;

        return new Entry(id, spell, title, description, null);
    }
}
