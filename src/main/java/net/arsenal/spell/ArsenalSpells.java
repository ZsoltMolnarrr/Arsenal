package net.arsenal.spell;

import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.fx.SpellEngineParticles;
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
        spell.tier = 7;

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
        spell.tier = 7;

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

//    public static Entry lesser_use_damage = add(lesser_use_damage());
//    private static Entry lesser_use_damage() {
//        var id = Identifier.of(ArsenalMod.NAMESPACE, "lesser_use_damage");
//        var description = "Use: Increases attack damage by {bonus} for {effect_duration} seconds.";
//        var effect = ArsenalEffects.LESSER_ATTACK_DAMAGE;
//        var title = effect.title;
//        SpellTooltip.DescriptionMutator mutator = (args) -> {
//            var modifier = effect.config().firstModifier();
//            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
//            return args.description().replace("{bonus}", bonus);
//        };
//
//        var spell = activeSpellBase();
//        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;
//
//        spell.release.animation = "spell_engine:dual_handed_weapon_charge";
//        spell.release.sound = new Sound(ArsenalSounds.SHARPEN.id().toString());
//        spell.release.particles = new ParticleBatch[]{
//                lesserActivateParticles(SpellEngineParticles.WHITE, 25)
//        };
//
//        var buff = createEffectImpact(effect.id.toString(), T1_USE_EFFECT_DURATION);
//        spell.impacts = List.of(buff);
//
//        spell.cost = new Spell.Cost();
//        spell.cost.cooldown = new Spell.Cost.Cooldown();
//        spell.cost.cooldown.duration = T1_USE_EFFECT_COOLDOWN;
//
//        return new Entry(id, spell, title, description, mutator);
//    }
}
