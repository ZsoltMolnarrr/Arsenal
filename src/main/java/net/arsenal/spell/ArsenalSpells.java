package net.arsenal.spell;

import net.arsenal.ArsenalMod;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
import net.spell_power.api.SpellSchools;
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
        spell.tooltip.show_header = false;
        spell.tooltip.name = new Spell.Tooltip.LineOptions(false, true);
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

//        spell.tooltip = new Spell.Tooltip();
//        spell.tooltip.name = new Spell.Tooltip.LineOptions(false, true);
//        spell.tooltip.description.color = Formatting.DARK_GREEN.asString();
//        spell.tooltip.description.show_in_compact = true;
        // spell.tooltip.show_header = false;

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
        spell.cost.cooldown.hosting_item = false;
    }

    private static final Identifier HOLY_DECELERATE = SpellEngineParticles.MagicParticles.get(
            SpellEngineParticles.MagicParticles.Shape.HOLY,
            SpellEngineParticles.MagicParticles.Motion.DECELERATE
    ).id();
    private static final Identifier SPARK_DECELERATE = SpellEngineParticles.MagicParticles.get(
            SpellEngineParticles.MagicParticles.Shape.SPARK,
            SpellEngineParticles.MagicParticles.Motion.DECELERATE
    ).id();
    private static final Identifier SPARK_FLOAT = SpellEngineParticles.MagicParticles.get(
            SpellEngineParticles.MagicParticles.Shape.SPARK,
            SpellEngineParticles.MagicParticles.Motion.FLOAT
    ).id();
    private static final Identifier STRIPE_FLOAT = SpellEngineParticles.MagicParticles.get(
            SpellEngineParticles.MagicParticles.Shape.STRIPE,
            SpellEngineParticles.MagicParticles.Motion.FLOAT
    ).id();

    private static Spell.Trigger killedByMeleeTrigger() {
        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        var deadCondition = new Spell.TargetCondition();
        deadCondition.health_percent_below = 0F;
        deadCondition.health_percent_above = 0F;
        trigger.target_conditions = List.of(deadCondition);
        return trigger;
    }

    private static void areaTarget(Spell spell, Identifier particleId, long particleColor) {
        spell.release.particles_scaled_with_ranged = new ParticleBatch[]{
                new ParticleBatch(particleId.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.GROUND,
                        1, 0.0F, 0.F)
                        .color(particleColor)
        };

        spell.target = new Spell.Target();
        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
    }

    private static void buffAreaTarget(Spell spell, Identifier particleId, long particleColor) {
        areaTarget(spell, particleId, particleColor);
        spell.target.area.include_caster = true;
    }

    private static long HOLY_COLOR = Color.HOLY.toRGBA();
    public static Entry radiance_melee = add(radiance_melee());
    private static Entry radiance_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "radiance_melee");
        var title = "Radiance";
        var description = "On melee hit: {trigger_chance} chance to heal yourself and nearby allies by {heal}.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.HEALING;
        spell.range = 2F;

        var trigger = new Spell.Trigger();
        trigger.chance = 0.25F;
        trigger.chance_batching = true;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        trigger.aoe_source_override = Spell.Trigger.TargetSelector.CASTER;
        spell.passive.triggers = List.of(trigger);

        radianceTargetAndImpact(spell, EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString());
        configureCooldown(spell, 3F);
        spell.cost.cooldown.hosting_item = false;

        return new Entry(id, spell, title, description, null);
    }

    public static Entry radiance_ranged = add(radiance_ranged());
    private static Entry radiance_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "radiance_ranged");
        var title = "Radiance";
        var description = "On arrow hit: {trigger_chance} chance to heal yourself and nearby allies by {heal}.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.HEALING;
        spell.range = 2F;

        var trigger = new Spell.Trigger();
        trigger.chance = 0.25F;
        trigger.type = Spell.Trigger.Type.ARROW_IMPACT;
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        trigger.aoe_source_override = Spell.Trigger.TargetSelector.CASTER;
        spell.passive.triggers = List.of(trigger);

        radianceTargetAndImpact(spell, EntityAttributes_RangedWeapon.DAMAGE.id.toString());
        configureCooldown(spell, 0.5F);
        spell.cost.cooldown.hosting_item = false;

        return new Entry(id, spell, title, description, null);
    }

    public static Entry radiance_spell = add(radiance_spell());
    private static Entry radiance_spell() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "radiance_spell");
        var title = "Radiance";
        var description = "On spell cast: {trigger_chance} chance to heal yourself and nearby allies by {heal}.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.HEALING;
        spell.range = 2F;

        var trigger = new Spell.Trigger();
        trigger.chance = 0.25F;
        trigger.chance_batching = true;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.type = Spell.Trigger.Type.SPELL_CAST;
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        trigger.aoe_source_override = Spell.Trigger.TargetSelector.CASTER;
        spell.passive.triggers = List.of(trigger);

        radianceTargetAndImpact(spell, null);
        configureCooldown(spell, 5F);
        spell.cost.cooldown.hosting_item = false;

        return new Entry(id, spell, title, description, null);
    }

    private static void radianceTargetAndImpact(Spell spell, @Nullable String attribute) {
        buffAreaTarget(spell, SpellEngineParticles.area_effect_658.id(), Color.HOLY.toRGBA());

        var heal = new Spell.Impact();
        if (attribute != null) {
            heal.attribute = attribute;
        }
        heal.action = new Spell.Impact.Action();
        heal.action.type = Spell.Impact.Action.Type.HEAL;
        heal.action.heal = new Spell.Impact.Action.Heal();
        heal.action.heal.spell_power_coefficient = 0.25F;
        heal.particles = new ParticleBatch[]{
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        20, 0.1F, 0.1F)
                        .color(HOLY_COLOR),
                new ParticleBatch(
                        SpellEngineParticles.area_circle_1.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.FEET,
                        1, 0.2F, 0.2F)
                        .followEntity(true)
                        .scale(0.8F)
                        .maxAge(0.8F)
                        .color(Color.HOLY.toRGBA()),
                new ParticleBatch(
                        HOLY_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.2F, 0.25F)
                        .color(HOLY_COLOR)
        };
        heal.sound = new Sound(SpellEngineSounds.GENERIC_HEALING_IMPACT_1.id().toString());
        spell.impacts = List.of(heal);
    }

    public static Entry stunning_melee = add(stunning_melee());
    private static Entry stunning_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "stunning_melee");
        var title = "Stunning";
        var description = "On melee hit: {trigger_chance} chance to stun the targets for {effect_duration} seconds.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.chance_batching = true;
        trigger.chance = 0.2F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var stun = createEffectImpact(ArsenalEffects.STUN.id.toString(), T3_PERK_CC_DURATION);
        stun.sound = new Sound(SpellEngineSounds.STUN_GENERIC.id().toString());
        spell.impacts = List.of(stun);

        configureCooldown(spell, T3_PERK_CC_COOLDOWN);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, null);
    }

    public static Entry exploding_melee = add(exploding_melee());
    private static Entry exploding_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "exploding_melee");
        var title = "Exploding";
        var description = "On melee hit: {trigger_chance} chance to cause fiery explosion on a target, dealing {damage} damage.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.chance = 0.2F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var explosion = new Spell.Impact();
        explosion.action = new Spell.Impact.Action();
        explosion.action.type = Spell.Impact.Action.Type.DAMAGE;
        explosion.action.damage = new Spell.Impact.Action.Damage();
        explosion.action.damage.spell_power_coefficient = 0.5F;
        spell.impacts = List.of(explosion);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 2.5F;
        spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.area_impact.particles = new ParticleBatch[]{
                new ParticleBatch(
                        SpellEngineParticles.fire_explosion.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1, 0F, 0.1F)
        };
        spell.area_impact.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IMPACT_1.id().toString());

        return new Entry(id, spell, title, description, null);
    }

    public static Entry wither_melee = add(wither_melee());
    private static Entry wither_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "wither_melee");
        var title = "Withering";
        var description = "On melee hit: {trigger_chance} chance to inflict the target with strong Wither effect for {effect_duration} seconds.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.chance = 0.2F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        witherImpact(spell, 0.2F);

        configureCooldown(spell, 3);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, null);
    }

    public static Entry wither_ranged = add(wither_ranged());
    private static Entry wither_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "wither_ranged");
        var title = "Withering";
        var description = "On arrow hit: {trigger_chance} chance to inflict the target with strong Wither effect for {effect_duration} seconds.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_RANGED;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.ARROW_IMPACT;
        trigger.chance = 0.3F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        witherImpact(spell, 0.2F);

        configureCooldown(spell, 3);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, null);
    }

    private static void witherImpact(Spell spell, float amplifier_multiplier) {
        var wither = createEffectImpact("wither", 5);
        wither.action.status_effect.amplifier_power_multiplier = amplifier_multiplier;
        wither.action.status_effect.show_particles = true;
        final var witherParticles = new ParticleBatch("infested",
                ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                50, 0.1F, 0.2F);
        wither.particles = new ParticleBatch[]{
                witherParticles
        };
        spell.impacts = List.of(wither);
    }

    public static Entry flame_cloud_melee = add(flame_cloud_melee());
    private static Entry flame_cloud_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "flame_cloud_melee");
        var title = "Flame Strike";
        var description = "On melee hit: {trigger_chance} chance to ignite the area around the target, dealing {damage} damage per second.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.chance = 0.2F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        flameCloud(spell, 0.4F);

        configureCooldown(spell, 3);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, null);
    }

    public static Entry flame_cloud_ranged = add(flame_cloud_ranged());
    private static Entry flame_cloud_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "flame_cloud_ranged");
        var title = "Flame Strike";
        var description = "On arrow hit: {trigger_chance} chance to ignite the area around the target, dealing {damage} damage per second.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_RANGED;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.ARROW_IMPACT;
        trigger.chance = 0.3F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        flameCloud(spell, 0.3F);

        // configureCooldown(spell, 3);
        // spell.cost.batching = true;

        return new Entry(id, spell, title, description, null);
    }

    private static void flameCloud(Spell spell, float coefficient) {
        spell.deliver.type = Spell.Delivery.Type.CLOUD;
        spell.deliver.delay = 10;
        var cloud = new Spell.Delivery.Cloud();
        cloud.volume.radius = 2;
        cloud.volume.area.vertical_range_multiplier = 0.3F;
        cloud.volume.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IMPACT_2.id().toString());
        cloud.impact_tick_interval = 8;
        cloud.time_to_live_seconds = 4;
        cloud.spawn.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IGNITE.id().toString());
        cloud.client_data = new Spell.Delivery.Cloud.ClientData();
        cloud.client_data.light_level = 15;
        cloud.client_data.particles = new ParticleBatch[] {
                new ParticleBatch(SpellEngineParticles.flame_ground.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        3, 0, 0),
                new ParticleBatch(SpellEngineParticles.flame_medium_a.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        2, 0.02F, 0.1F),
                new ParticleBatch(SpellEngineParticles.flame_medium_b.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        1, 0.02F, 0.1F),
                new ParticleBatch(SpellEngineParticles.flame_spark.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        3, 0.03F, 0.2F),
        };
        spell.deliver.clouds = List.of(cloud);

        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.knockback = 0.5F;
        damage.action.damage.spell_power_coefficient = coefficient;
        damage.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IMPACT_1.id().toString());
        damage.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.flame.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        20, 0.05F, 0.15F),
                new ParticleBatch(SpellEngineParticles.flame_medium_a.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        20, 0.05F, 0.15F),
        };
        spell.impacts = List.of(damage);
    }

    public static Entry poison_cloud_melee = add(poison_cloud_melee());
    private static Entry poison_cloud_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "poison_cloud_melee");
        var title = "Poison Cloud";
        var description = "On melee hit: {trigger_chance} chance to create a toxic cloud around the target, lasting for {effect_duration} seconds.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.chance = 0.2F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        poisonCloud(spell, 0.4F);

        configureCooldown(spell, 1);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, null);
    }

    public static Entry poison_cloud_ranged = add(poison_cloud_ranged());
    private static Entry poison_cloud_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "poison_cloud_ranged");
        var title = "Poison Cloud";
        var description = "On arrow hit: {trigger_chance} chance to create a toxic cloud around the target, lasting for {effect_duration} seconds.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_RANGED;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.ARROW_IMPACT;
        trigger.chance = 0.3F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        poisonCloud(spell, 0.3F);

        // configureCooldown(spell, 3);
        // spell.cost.batching = true;

        return new Entry(id, spell, title, description, null);
    }

    private static void poisonCloud(Spell spell, float coefficient) {
        spell.deliver.type = Spell.Delivery.Type.CLOUD;
        spell.deliver.delay = 8;
        var cloud = new Spell.Delivery.Cloud();
        cloud.volume.radius = 2;
        cloud.volume.area.vertical_range_multiplier = 0.3F;
        cloud.volume.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IMPACT_1.id().toString());
        cloud.impact_tick_interval = 8;
        cloud.time_to_live_seconds = 3;
        cloud.spawn.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IGNITE.id().toString());
        cloud.client_data = new Spell.Delivery.Cloud.ClientData();
        cloud.client_data.light_level = 0;
        cloud.client_data.particles = new ParticleBatch[] {
                new ParticleBatch(SpellEngineParticles.smoke_large.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        1, 0.01F, 0.02F)
                        .color(0x99FF66AAL),
                new ParticleBatch(SpellEngineParticles.smoke_large.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        1, 0.01F, 0.02F)
                        .color(0x33DD33EE),
        };
        spell.deliver.clouds = List.of(cloud);

        var impact = new Spell.Impact();
        impact.action = new Spell.Impact.Action();
        impact.action.type = Spell.Impact.Action.Type.STATUS_EFFECT;
        impact.action.status_effect = new Spell.Impact.Action.StatusEffect();
        impact.action.status_effect.effect_id = "poison";
        impact.action.status_effect.show_particles = true;
        impact.action.status_effect.duration = 5;
        impact.action.status_effect.amplifier_power_multiplier = coefficient;
        impact.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IMPACT_2.id().toString());
        impact.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.smoke_large.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        0.5F, 0.01F, 0.02F)
                        .color(0x33DD33AA),
        };
        spell.impacts = List.of(impact);
    }

    public static Entry slowing_melee = add(slowing_melee());
    private static Entry slowing_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "slowing_melee");
        var title = "Frostbite";
        var description = "On melee hit: {trigger_chance} chance to slow movement and attack speed of the the target by {bonus}, for {effect_duration} seconds.";
        var effect = ArsenalEffects.SLOWING;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            return args.description().replace("{bonus}", bonus);
        };

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.chance_batching = true;
        trigger.chance = 0.2F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var slow = createEffectImpact(ArsenalEffects.SLOWING.id.toString(), 4);
        slow.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.1F, 0.15F)
        };
        slow.sound = new Sound(SpellEngineSounds.STUN_GENERIC.id().toString());
        spell.impacts = List.of(slow);

        configureCooldown(spell, 3);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, mutator);
    }

    public static Color LEECHING_COLOR = Color.from(0xff3333);
    public static Entry leeching_melee = add(leeching_melee());
    private static Entry leeching_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "leeching_melee");
        var title = "Leeching";
        var description = "Defeating enemies heals you by a small portion of their max health.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        spell.passive.triggers = List.of(killedByMeleeTrigger());

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var leech = new Spell.Impact();
        leech.attribute = EntityAttributes.GENERIC_MAX_HEALTH.getIdAsString();
        leech.attribute_from_target = true;
        leech.action = new Spell.Impact.Action();
        leech.action.apply_to_caster = true;
        leech.action.type = Spell.Impact.Action.Type.HEAL;
        leech.action.heal = new Spell.Impact.Action.Heal();
        leech.action.heal.spell_power_coefficient = 0.05F;
        leech.particles = new ParticleBatch[]{
                new ParticleBatch(SPARK_FLOAT.toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.CENTER,
                        25, 0.05F, 0.1F)
                        .color(LEECHING_COLOR.toRGBA()),
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.08F, 0.12F)
                        .invert()
                        .preSpawnTravel(5)
                        .followEntity(true)
                        .color(LEECHING_COLOR.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.ground_glow.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.GROUND,
                        1, 0.0F, 0.F)
                        .followEntity(true)
                        .scale(0.8F)
                        .color(LEECHING_COLOR.alpha(0.2F).toRGBA())
        };
        leech.sound = new Sound(SpellEngineSounds.GENERIC_HEALING_IMPACT_1.id().toString());
        spell.impacts = List.of(leech);

        return new Entry(id, spell, title, description, null);
    }

    public static Entry swirling_melee = add(swirling_melee());
    private static Entry swirling_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "swirling_melee");
        var title = "Swirling";
        var description = "The last attack in a combo performs a swirling attack, dealing {damage} damage to nearby enemies.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;
        spell.range = -0.5F;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.melee = new Spell.Trigger.MeleeCondition();
        trigger.melee.is_combo = true;
        trigger.melee.is_offhand = false;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.distance_dropoff = Spell.Target.Area.DropoffCurve.NONE;
        spell.target.area.vertical_range_multiplier = 0.5F;

        spell.release.particles_scaled_with_ranged = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.area_swirl.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1, 0.0F, 0.F)
                        .scale(0.8F)
                        .followEntity(true)
        };

        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.spell_power_coefficient = 0.5F;
        damage.action.damage.knockback = 0.5F;
        // damage.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IMPACT_1.id().toString());
        spell.impacts = List.of(damage);

        return new Entry(id, spell, title, description, null);
    }

    public static final Color GUARDING_COLOR = Color.from(0x66ccff);
    public static Entry guarding_strike_melee = add(guarding_strike_melee());
    private static Entry guarding_strike_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "guarding_strike_melee");
        var title = "Guarding Strike";
        var effect = ArsenalEffects.GUARDING;
        var description = "Defeating enemies grants you and nearby allies a temporary effect reducing damage taken by {bonus}, lasting {effect_duration} seconds.";
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(Math.abs(modifier.value), modifier.operation);
            return args.description().replace("{bonus}", bonus);
        };

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;
        spell.range = 2F;

        var trigger = killedByMeleeTrigger();
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        trigger.aoe_source_override = Spell.Trigger.TargetSelector.CASTER;
        spell.passive.triggers = List.of(trigger);

        buffAreaTarget(spell, SpellEngineParticles.area_effect_714.id(), GUARDING_COLOR.toRGBA());

        var buff = createEffectImpact(ArsenalEffects.GUARDING.id.toString(), 5);
        buff.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.sign_shield.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.CENTER,
                        1, 0.65F, 0.65F)
                        .scale(0.8F)
                        .color(GUARDING_COLOR.alpha(0.75F).toRGBA())
                        .followEntity(true)
        };
        spell.impacts = List.of(buff);
        configureCooldown(spell, 10);

        return new Entry(id, spell, title, description, mutator);
    }

    public static Color SUNDERING_COLOR = Color.from(0x595959);
    public static Entry sundering_melee = add(sundering_melee());
    private static Entry sundering_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "sundering_melee");
        var title = "Sundering";
        var description = "On melee hit: {trigger_chance} chance to reduce the target's armor by {bonus} for {effect_duration} seconds.";
        var effect = ArsenalEffects.SUNDERING;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(Math.abs(modifier.value), modifier.operation);
            return args.description().replace("{bonus}", bonus);
        };

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.chance_batching = true;
        trigger.chance = 0.2F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var sunder = createEffectImpact(ArsenalEffects.SUNDERING.id.toString(), 5);
        sunder.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.1F, 0.1F)
                        .color(SUNDERING_COLOR.toRGBA())
        };
        spell.impacts = List.of(sunder);

        configureCooldown(spell, 5);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, mutator);
    }

    public static Color RAMPAGING_COLOR = Color.from(0xff471a);
    public static Entry rampaging_melee = add(rampaging_melee());
    private static Entry rampaging_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "rampaging_melee");
        var title = "Rampaging";
        var description = "Defeating a mob grants " + title + " effect, increasing your damage by {bonus}, stacking up to {effect_amplifier} times, lasting {effect_duration} seconds.";
        var effect = ArsenalEffects.RAMPAGING;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(Math.abs(modifier.value), modifier.operation);
            return args.description().replace("{bonus}", bonus);
        };

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = killedByMeleeTrigger();
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        spell.passive.triggers = List.of(trigger);

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        spell.deliver.stash_effect = new Spell.Delivery.StashEffect();
        spell.deliver.stash_effect.id = effect.id.toString();
        spell.deliver.stash_effect.consume = 0;
        spell.deliver.stash_effect.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var buff = createEffectImpact(ArsenalEffects.RAMPAGING.id.toString(), 10);
        buff.particles = new ParticleBatch[]{
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.3F, 0.35F)
                        .color(RAMPAGING_COLOR.toRGBA())
        };
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.amplifier = 4;
        buff.action.status_effect.refresh_duration = false;
        spell.impacts = List.of(buff);

        configureCooldown(spell, 20);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, mutator);
    }

    public static Color UNYIELDING_COLOR = Color.from(0x33ccff);
    public static Entry unyielding_shield = add(unyielding_shield());
    private static Entry unyielding_shield() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "unyielding_shield");
        var title = "Unyielding";
        var description = "Blocking grants you increased knockback resistance and armor toughness, lasting {effect_duration} seconds.";
        var effect = ArsenalEffects.UNYIELDING;

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SHIELD_BLOCK;
        spell.passive.triggers = List.of(trigger);

        var duration = 5;

        var buff = createEffectImpact(effect.id.toString(), duration);
        buff.particles = new ParticleBatch[]{
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.3F, 0.35F)
                        .color(RAMPAGING_COLOR.toRGBA())
        };
        spell.impacts = List.of(buff);

        configureCooldown(spell, duration * 2);

        return new Entry(id, spell, title, description, null);
    }

    public static Entry spiked_shield = add(spiked_shield());
    private static Entry spiked_shield() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "spiked_shield");
        var title = "Spiked";
        var description = "On shield block: {trigger_chance} chance to deal {damage} damage to the attacker.";

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = new Spell.Trigger();
        trigger.chance = 0.5F;
        trigger.type = Spell.Trigger.Type.SHIELD_BLOCK;
        trigger.target_override = Spell.Trigger.TargetSelector.TARGET;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.min_power = 10;
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.spell_power_coefficient = 0.25F;
        damage.action.damage.knockback = 0.25F;

        spell.impacts = List.of(damage);

        return new Entry(id, spell, title, description, null);
    }
}
