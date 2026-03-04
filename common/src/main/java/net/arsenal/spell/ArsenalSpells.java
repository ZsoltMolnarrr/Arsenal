package net.arsenal.spell;

import net.arsenal.ArsenalMod;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
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
import java.util.EnumSet;
import java.util.List;

public class ArsenalSpells {
    public enum Category {
        MELEE, RANGED, SPELL, HEAL, SHIELD
    }
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator, EnumSet<Category> categories) {
        public Entry(Identifier id, Spell spell, String title, String description,
                     @Nullable SpellTooltip.DescriptionMutator mutator, Category category) {
            this(id, spell, title, description, mutator, EnumSet.of(category));
        }
    }

    public static final List<Entry> all = new ArrayList<>();
    private static Entry add(Entry entry) {
        all.add(entry);
        return entry;
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
    private static final Identifier SPELL_ASCEND = SpellEngineParticles.MagicParticles.get(
            SpellEngineParticles.MagicParticles.Shape.SPELL,
            SpellEngineParticles.MagicParticles.Motion.ASCEND
    ).id();

    private static Spell.TargetCondition deadCondition() {
        var deadCondition = new Spell.TargetCondition();
        deadCondition.health_percent_below = 0F;
        deadCondition.health_percent_above = 0F;
        return deadCondition;
    }

    private static Spell.TargetCondition weakCondition() {
        var deadCondition = new Spell.TargetCondition();
        deadCondition.health_percent_below = 0.5F;
        deadCondition.health_percent_above = 0.01F;
        return deadCondition;
    }

    private static Spell.Trigger killedBySpellTrigger() {
        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        var deadCondition = deadCondition();
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

    private static Spell.Impact damageImpact(float coefficient, float knockback) {
        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.spell_power_coefficient = coefficient;
        damage.action.damage.knockback = knockback;
        return damage;
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

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry radiance_ranged = add(radiance_ranged());
    private static Entry radiance_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "radiance_ranged");
        var title = "Radiance";
        var description = "On arrow hit: {trigger_chance} chance to heal yourself and nearby allies by {heal}.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.HEALING;
        spell.range = 2F;

        var trigger = SpellBuilder.Triggers.arrowHit();
        trigger.chance = 0.25F;
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        trigger.aoe_source_override = Spell.Trigger.TargetSelector.CASTER;
        spell.passive.triggers = List.of(trigger);

        radianceTargetAndImpact(spell, EntityAttributes_RangedWeapon.DAMAGE.id.toString());
        configureCooldown(spell, 0.5F);
        spell.cost.cooldown.hosting_item = false;

        return new Entry(id, spell, title, description, null, Category.RANGED);
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

        return new Entry(id, spell, title, description, null, EnumSet.of(Category.SPELL, Category.HEAL));
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
        heal.sound = new Sound(ArsenalSounds.radiance_impact.id().toString());
        spell.impacts = List.of(heal);
    }

    public static Entry stunning_melee = add(stunning_melee());
    private static Entry stunning_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "stunning_melee");
        var title = "Stunning";
        var description = "On melee hit: {trigger_chance} chance to stun the targets for {effect_duration} seconds.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var triggers =  SpellBuilder.Triggers.withConditionMustWield(
                List.of(SpellBuilder.Triggers.meleeAttackImpact())
        );
        triggers.forEach(trigger -> {
            trigger.chance_batching = true;
            trigger.chance = 0.2F;
        });
        spell.passive.triggers = triggers;

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var stun = createEffectImpact(ArsenalEffects.STUN.id.toString(), 2);
        stun.sound = new Sound(SpellEngineSounds.STUN_GENERIC.id().toString());
        spell.impacts = List.of(stun);

        configureCooldown(spell, 20);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry exploding_melee = add(exploding_melee());
    private static Entry exploding_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "exploding_melee");
        var title = "Exploding";
        var description = "On melee hit: {trigger_chance} chance to cause fiery explosion on a target, dealing {damage} damage.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var triggers =  SpellBuilder.Triggers.withConditionMustWield(
                List.of(SpellBuilder.Triggers.meleeAttackImpact())
        );
        triggers.forEach(trigger -> {
            trigger.chance = 0.2F;
        });
        spell.passive.triggers = triggers;
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

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static final Color WITHER_COLOR = Color.from(0x333333);
    public static Entry wither_melee = add(wither_melee());
    private static Entry wither_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "wither_melee");
        var title = "Withering";
        var description = "On melee hit: {trigger_chance_1} chance to inflict the target with strong Wither effect for {effect_duration} seconds.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var triggers =  SpellBuilder.Triggers.withConditionMustWield(
                SpellBuilder.Triggers.meleeImpact()
        );
        triggers.forEach(trigger -> {
            trigger.chance = 0.2F;
        });
        spell.passive.triggers = triggers;

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        witherImpact(spell, 0.2F);

        configureCooldown(spell, 3);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry wither_ranged = add(wither_ranged());
    private static Entry wither_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "wither_ranged");
        var title = "Withering";
        var description = "On arrow hit: {trigger_chance} chance to inflict the target with strong Wither effect for {effect_duration} seconds.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_RANGED;

        var trigger = SpellBuilder.Triggers.arrowHit();
        trigger.chance = 0.3F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        witherImpact(spell, 0.25F);

        configureCooldown(spell, 3);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, null, Category.RANGED);
    }

    private static void witherImpact(Spell spell, float amplifier_multiplier) {
        var wither = createEffectImpact("wither", 5);
        wither.action.status_effect.amplifier_power_multiplier = amplifier_multiplier;
        wither.action.status_effect.show_particles = true;
        wither.particles = new ParticleBatch[]{
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SKULL,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.2F, 0.25F)
                        .color(WITHER_COLOR.toRGBA())
        };
        wither.sound = new Sound(ArsenalSounds.wither_impact.id().toString());
        spell.impacts = List.of(wither);
    }

    public static Entry flame_cloud_melee = add(flame_cloud_melee());
    private static Entry flame_cloud_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "flame_cloud_melee");
        var title = "Flame Strike";
        var description = "On melee hit: {trigger_chance} chance to ignite the area around the target, dealing {damage} damage per second.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var triggers =  SpellBuilder.Triggers.withConditionMustWield(
                List.of(SpellBuilder.Triggers.meleeAttackImpact())
        );
        triggers.forEach(trigger -> {
            trigger.chance = 0.2F;
        });
        spell.passive.triggers = triggers;

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        flameCloud(spell, 0.25F, EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString());

        configureCooldown(spell, 3);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry flame_cloud_ranged = add(flame_cloud_ranged());
    private static Entry flame_cloud_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "flame_cloud_ranged");
        var title = "Flame Strike";
        var description = "On arrow hit: {trigger_chance} chance to ignite the area around the target, dealing {damage} damage per second.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.FIRE;

        var trigger = SpellBuilder.Triggers.arrowHit();
        trigger.chance = 0.3F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        flameCloud(spell, 0.25F, EntityAttributes_RangedWeapon.DAMAGE.id.toString());

        // configureCooldown(spell, 3);
        // spell.cost.batching = true;

        return new Entry(id, spell, title, description, null, Category.RANGED);
    }

    public static Entry flame_cloud_spell = add(flame_cloud_spell());
    private static Entry flame_cloud_spell() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "flame_cloud_spell");
        var title = "Flame Strike";
        var description = "On spell hit: {trigger_chance} chance to ignite the area around the target, dealing {damage} damage per second.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.FIRE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.type = Spell.Type.ACTIVE;
        trigger.chance = 0.3F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        flameCloud(spell, 0.25F, null);

        configureCooldown(spell, 2);
        // spell.cost.batching = true;

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    private static void flameCloud(Spell spell, float coefficient, @Nullable String attribute) {
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
        if (attribute != null) {
            damage.attribute = attribute;
        }
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

        var triggers =  SpellBuilder.Triggers.withConditionMustWield(
                List.of(SpellBuilder.Triggers.meleeAttackImpact())
        );
        triggers.forEach(trigger -> {
            trigger.chance = 0.2F;
        });
        spell.passive.triggers = triggers;

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var duration = 6;
        poisonCloud(spell, 0.25F, duration);

        configureCooldown(spell, duration * 0.5F);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry poison_cloud_ranged = add(poison_cloud_ranged());
    private static Entry poison_cloud_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "poison_cloud_ranged");
        var title = "Poison Cloud";
        var description = "On arrow hit: {trigger_chance} chance to create a toxic cloud around the target, lasting for {effect_duration} seconds.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_RANGED;

        var trigger = SpellBuilder.Triggers.arrowHit();
        trigger.chance = 0.3F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var duration = 8;
        poisonCloud(spell, 0.25F, duration);

        configureCooldown(spell, duration * 0.5F);
        // spell.cost.batching = true;

        return new Entry(id, spell, title, description, null, Category.RANGED);
    }

    private static void poisonCloud(Spell spell, float coefficient, float cloudDuration) {
        spell.deliver.type = Spell.Delivery.Type.CLOUD;
        spell.deliver.delay = 8;
        var cloud = new Spell.Delivery.Cloud();
        cloud.volume.radius = 2;
        cloud.volume.area.vertical_range_multiplier = 0.3F;
        cloud.volume.sound = new Sound(ArsenalSounds.poison_cloud_tick.id().toString());
        cloud.impact_tick_interval = 8;
        cloud.time_to_live_seconds = cloudDuration;
        cloud.spawn.sound = new Sound(ArsenalSounds.poison_cloud_spawn.id().toString());
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

         var impact = SpellBuilder.Impacts.effectAdd_ScaledCap("poison", 5, coefficient);
//        var impact = SpellBuilder.Impacts.effectSet_ScaledAmplifier("poison", 5, 0, coefficient);
//        var impact = new Spell.Impact();
//        impact.action = new Spell.Impact.Action();
//        impact.action.type = Spell.Impact.Action.Type.STATUS_EFFECT;
//        impact.action.status_effect = new Spell.Impact.Action.StatusEffect();
//        impact.action.status_effect.effect_id = "poison";
//
//        impact.action.status_effect.show_particles = true;
//        impact.action.status_effect.duration = 5;
//        impact.action.status_effect.amplifier_power_multiplier = coefficient;
        // impact.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IMPACT_2.id().toString());
        impact.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.smoke_large.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        0.5F, 0.01F, 0.02F)
                        .color(0x33DD33AA),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SKULL,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        3, 0.1F, 0.2F)
                        .color(0x33DD33AA)
        };
        spell.impacts = List.of(impact);
    }

    public static Entry slowing_melee = add(slowing_melee());
    private static Entry slowing_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "slowing_melee");
        var title = "Frostbite";
        var description = "On melee hit: {trigger_chance} chance to slow movement and attack speed of the the target by {bonus}, for {effect_duration} seconds.";
        var effect = ArsenalEffects.FROSTBITE;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            return args.description().replace("{bonus}", bonus);
        };

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var triggers =  SpellBuilder.Triggers.withConditionMustWield(
                List.of(SpellBuilder.Triggers.meleeAttackImpact())
        );
        triggers.forEach(trigger -> {
            trigger.chance_batching = true;
            trigger.chance = 0.2F;
        });
        spell.passive.triggers = triggers;

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var slow = createEffectImpact(ArsenalEffects.FROSTBITE.id.toString(), 4);
        slow.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.1F, 0.15F)
        };
        slow.sound = new Sound(SpellEngineSounds.STUN_GENERIC.id().toString());
        spell.impacts = List.of(slow);

        configureCooldown(spell, 3);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, mutator, Category.MELEE);
    }

    public static Color LEECHING_COLOR = Color.from(0xff3333);
    public static Entry leeching_melee = add(leeching_melee());
    private static Entry leeching_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "leeching_melee");
        var title = "Leeching";
        var description = "Defeating enemies heals you by a small portion of their max health.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;
        spell.passive.triggers = SpellBuilder.Triggers.withConditionMustWield(
                SpellBuilder.Triggers.meleeKills()
        );
        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        leechingEffect(spell);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry leeching_spell = add(leeching_spell());
    private static Entry leeching_spell() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "leeching_spell");
        var title = "Leeching";
        var description = "Defeating enemies heals you by a small portion of their max health.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;
        spell.passive.triggers = List.of(killedBySpellTrigger());
        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        leechingEffect(spell);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    private static void leechingEffect(Spell spell) {
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
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.CENTER,
                        15, 0.02F, 0.1F)
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
        leech.sound = Sound.withVolume(ArsenalSounds.leeching_impact.id(), 0.6F);
        spell.impacts = List.of(leech);
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

        var trigger = SpellBuilder.Triggers.withConditionMustWield(
                SpellBuilder.Triggers.meleeAttackImpact()
        );
        trigger.melee = new Spell.Trigger.MeleeCondition();
        trigger.melee.is_combo = true;
        trigger.melee.is_offhand = false;
        spell.passive.triggers = List.of(trigger);

        spell.release.sound = new Sound(ArsenalSounds.swirling.id().toString());

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

        var damage = damageImpact(0.5F, 0.5F);
        spell.impacts = List.of(damage);

        return new Entry(id, spell, title, description, null, Category.MELEE);
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

        var triggers = SpellBuilder.Triggers.withConditionMustWield(
                SpellBuilder.Triggers.meleeKills()
        );
        for (var trigger : triggers) {
            trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
            trigger.aoe_source_override = Spell.Trigger.TargetSelector.CASTER;
        }
        spell.passive.triggers = triggers;

        spell.release.sound = new Sound(ArsenalSounds.guardian_strike_release.id().toString());

        buffAreaTarget(spell, SpellEngineParticles.area_effect_714.id(), GUARDING_COLOR.toRGBA());

        var buff = createEffectImpact(ArsenalEffects.GUARDING.id.toString(), 5);
        buff.particles = new ParticleBatch[]{
                new ParticleBatch(
                        SpellEngineParticles.area_circle_1.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.FEET,
                        1, 0.2F, 0.2F)
                        .followEntity(true)
                        .scale(0.8F)
                        .maxAge(0.4F)
                        .color(GUARDING_COLOR.toRGBA()),
                new ParticleBatch(SpellEngineParticles.sign_shield.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.CENTER,
                        1, 0.75F, 0.75F)
                        .scale(0.8F)
                        .color(GUARDING_COLOR.alpha(0.75F).toRGBA())
                        .followEntity(true)
        };
        buff.sound = new Sound(ArsenalSounds.guardian_strike_impact.id().toString());
        spell.impacts = List.of(buff);
        configureCooldown(spell, 10);

        return new Entry(id, spell, title, description, mutator, Category.MELEE);
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

        var trigger = SpellBuilder.Triggers.withConditionMustWield(
                SpellBuilder.Triggers.meleeAttackImpact()
        );
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
        sunder.sound = new Sound(ArsenalSounds.sunder_impact.id().toString());
        spell.impacts = List.of(sunder);

        configureCooldown(spell, 5);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, mutator, Category.MELEE);
    }

    public static Color UNYIELDING_COLOR = Color.from(0xff4a53);
    public static Entry unyielding_shield = add(unyielding_shield());
    private static Entry unyielding_shield() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "unyielding_shield");
        var title = "Unyielding";
        var description = "Blocking grants you increased knockback resistance and armor toughness, lasting {effect_duration} seconds.";
        var effect = ArsenalEffects.UNYIELDING;

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = SpellBuilder.Triggers.shieldBlock();
        spell.passive.triggers = List.of(trigger);

        var duration = 5;

        var buff = createEffectImpact(effect.id.toString(), duration);
        buff.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.sign_shield.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.CENTER,
                        1, 0.75F, 0.75F)
                        .scale(0.8F)
                        .color(UNYIELDING_COLOR.alpha(0.75F).toRGBA())
                        .followEntity(true),
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.3F, 0.35F)
                        .color(UNYIELDING_COLOR.toRGBA())
        };
        buff.sound = new Sound(ArsenalSounds.unyielding_impact.id().toString());
        spell.impacts = List.of(buff);

        configureCooldown(spell, duration * 2);

        return new Entry(id, spell, title, description, null, Category.SHIELD);
    }

    public static Entry guarding_shield = add(guarding_shield());
    private static Entry guarding_shield() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "guarding_shield");
        var title = "Guarding";
        var description = "On shield block: {trigger_chance} chance to reduce damage taken by {bonus}, lasting {effect_duration} seconds.";
        var effect = ArsenalEffects.GUARDING;

        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(Math.abs(modifier.value), modifier.operation);
            return args.description().replace("{bonus}", bonus);
        };

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = SpellBuilder.Triggers.shieldBlock();
        trigger.chance = 0.3F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        buffAreaTarget(spell, SpellEngineParticles.area_effect_714.id(), GUARDING_COLOR.toRGBA());

        var buff = createEffectImpact(ArsenalEffects.GUARDING.id.toString(), 5);
        buff.particles = new ParticleBatch[]{
                new ParticleBatch(
                        SpellEngineParticles.area_circle_1.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.FEET,
                        1, 0.2F, 0.2F)
                        .followEntity(true)
                        .scale(0.8F)
                        .maxAge(0.4F)
                        .color(GUARDING_COLOR.toRGBA()),
                new ParticleBatch(SpellEngineParticles.sign_shield.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.CENTER,
                        1, 0.75F, 0.75F)
                        .scale(0.8F)
                        .color(GUARDING_COLOR.alpha(0.75F).toRGBA())
                        .followEntity(true)
        };
        buff.sound = new Sound(ArsenalSounds.guardian_strike_impact.id().toString());
        spell.impacts = List.of(buff);
        configureCooldown(spell, 10);

        return new Entry(id, spell, title, description, mutator, Category.SHIELD);
    }

    public static final Color SPIKED_COLOR = Color.from(0xbfbfbf);
    public static Entry spiked_shield = add(spiked_shield());
    private static Entry spiked_shield() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "spiked_shield");
        var title = "Spiked";
        var description = "On shield block: {trigger_chance} chance to deal {damage} damage to the attacker.";

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = SpellBuilder.Triggers.shieldBlock();
        trigger.chance = 0.5F;
        trigger.target_override = Spell.Trigger.TargetSelector.TARGET;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var damage = damageImpact(0.25F, 0.25F);
        damage.action.min_power = 10;
        damage.particles = new ParticleBatch[]{
                new ParticleBatch(SPARK_FLOAT.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.3F, 0.35F)
                        .color(SPIKED_COLOR.toRGBA())
        };
        damage.sound = new Sound(ArsenalSounds.spike_impact.id().toString());

        spell.impacts = List.of(damage);

        return new Entry(id, spell, title, description, null, Category.SHIELD);
    }

    public static Entry bonus_shot_ranged = add(bonus_shot_ranged());
    private static Entry bonus_shot_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "bonus_shot_ranged");
        var title = "Bonus Shot";
        var description = "On arrow hit: {trigger_chance} chance to shoot an additional arrow.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_RANGED;

        var trigger = SpellBuilder.Triggers.arrowShot();
        trigger.chance = 0.2F;
        trigger.fire_delay = 1;
        spell.passive.triggers = List.of(trigger);

        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.LAUNCH_POINT,
                        25, 0.2F, 0.7F)
                        .rotate(ParticleBatch.Rotation.LOOK)
        };

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.SHOOT_ARROW;
        spell.deliver.shoot_arrow = new Spell.Delivery.ShootArrow();
        spell.deliver.shoot_arrow.launch_properties.velocity = 3.15F;
        spell.deliver.delay = 2;

        spell.arrow_perks = new Spell.ArrowPerks();
        spell.arrow_perks.damage_multiplier = 1F;
        spell.arrow_perks.bypass_iframes = true;
        spell.arrow_perks.knockback = 0.5F;

        configureCooldown(spell ,1);

        return new Entry(id, spell, title, description, null, Category.RANGED);
    }

    private static final float RAMPAGING_DURATION = 12;
    private static final float RAMPAGING_COOLDOWN = 20;

    public static Color RAMPAGING_COLOR = Color.from(0xff471a);
    public static Entry rampaging_melee = add(rampaging_melee());
    private static Entry rampaging_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "rampaging_melee");
        var title = "Rampaging";
        var description = "Defeating enemies grants " + title + " effect, increasing your damage by {bonus}, stacking up to {effect_amplifier_cap} times, lasting {effect_duration} seconds.";
        var effect = ArsenalEffects.RAMPAGING;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(Math.abs(modifier.value), modifier.operation);
            return args.description().replace("{bonus}", bonus);
        };

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var triggers = SpellBuilder.Triggers.withConditionMustWield(
                SpellBuilder.Triggers.meleeKills()
        );
        for (var trigger : triggers) {
            trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        }
        spell.passive.triggers = triggers;

        spell.release.sound = new Sound(ArsenalSounds.rampaging_activate.id().toString());

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        spell.deliver.stash_effect = new Spell.Delivery.StashEffect();
        spell.deliver.stash_effect.id = effect.id.toString();
        spell.deliver.stash_effect.consume = 0;
        spell.deliver.stash_effect.triggers = triggers;

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var buff = createEffectImpact(effect.id.toString(), RAMPAGING_DURATION);
        buff.particles = new ParticleBatch[]{
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.3F, 0.35F)
                        .color(RAMPAGING_COLOR.toRGBA())
        };
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.amplifier = 1;
        buff.action.status_effect.amplifier_cap = 4;
        buff.action.status_effect.refresh_duration = false;

        spell.impacts = List.of(buff);

        configureCooldown(spell, RAMPAGING_COOLDOWN);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, mutator, Category.MELEE);
    }

    public static Color FOCUSING_COLOR = Color.from(0x99ff66);
    public static Entry rampaging_ranged = add(rampaging_ranged());
    private static Entry rampaging_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "rampaging_ranged");
        var effect = ArsenalEffects.FOCUSING;
        var title = "Focusing";
        var description = "Defeating enemies grants " + effect.title + " effect, increasing your damage by {bonus}, stacking up to {effect_amplifier_cap} times, lasting {effect_duration} seconds.";
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(Math.abs(modifier.value), modifier.operation);
            return args.description().replace("{bonus}", bonus);
        };

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_RANGED;

        var triggers = SpellBuilder.Triggers.withConditionMustWield(
                SpellBuilder.Triggers.rangedKill()
        );
        triggers.forEach(trigger -> trigger.target_override = Spell.Trigger.TargetSelector.CASTER);
        spell.passive.triggers = triggers;

        spell.release.sound = new Sound(ArsenalSounds.focusing_activate.id().toString());

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        spell.deliver.stash_effect = new Spell.Delivery.StashEffect();
        spell.deliver.stash_effect.id = effect.id.toString();
        spell.deliver.stash_effect.consume = 0;
        spell.deliver.stash_effect.triggers = triggers;

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var buff = createEffectImpact(effect.id.toString(), RAMPAGING_DURATION);
        buff.particles = new ParticleBatch[]{
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.3F, 0.35F)
                        .color(FOCUSING_COLOR.toRGBA())
        };
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.amplifier = 1;
        buff.action.status_effect.amplifier_cap = 2;
        buff.action.status_effect.refresh_duration = false;

        spell.impacts = List.of(buff);

        configureCooldown(spell, RAMPAGING_COOLDOWN);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, mutator, Category.RANGED);
    }

    public static final Color SURGING_COLOR = Color.from(0x99ffff);
    public static Entry rampaging_spell = add(rampaging_spell());
    private static Entry rampaging_spell() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "rampaging_spell");
        var effect = ArsenalEffects.SURGING;
        var title = "Surging";
        var description = "Defeating enemies grants " + effect.title + " effect, increasing your spell critical chance by {bonus}, stacking up to {effect_amplifier_cap} times, lasting {effect_duration} seconds.";
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(Math.abs(modifier.value), modifier.operation);
            return args.description().replace("{bonus}", bonus);
        };

        var spell = passiveSpellBase();
        spell.school = SpellSchools.ARCANE;
        spell.release.sound = new Sound(ArsenalSounds.surging_activate.id().toString());

        var trigger = killedBySpellTrigger();
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        spell.passive.triggers = List.of(trigger);

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        spell.deliver.stash_effect = new Spell.Delivery.StashEffect();
        spell.deliver.stash_effect.id = effect.id.toString();
        spell.deliver.stash_effect.consume = 0;
        spell.deliver.stash_effect.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var duration = RAMPAGING_DURATION;
        var buff = createEffectImpact(effect.id.toString(), duration);
        buff.particles = new ParticleBatch[]{
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.3F, 0.35F)
                        .color(SURGING_COLOR.toRGBA())
        };
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.amplifier = 1;
        buff.action.status_effect.amplifier_cap = 4;
        buff.action.status_effect.refresh_duration = false;

        spell.impacts = List.of(buff);

        configureCooldown(spell, RAMPAGING_COOLDOWN);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, mutator, Category.SPELL);
    }

    public static final Color FROST_CLOUD_COLOR = Color.from(0xccffff);
    public static Entry frost_cloud_spell = add(frost_cloud_spell());
    private static Entry frost_cloud_spell() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "frost_cloud_spell");
        var title = "Frosty Puddle";
        var description = "On spell hit: {trigger_chance} chance to create a freezing zone around the target, slowing its movement and attack speed, lasting for {effect_duration} seconds.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.FROST;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.type = Spell.Type.ACTIVE;
        trigger.chance = 0.2F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        frostCloud(spell);

        configureCooldown(spell, 2);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    private static void frostCloud(Spell spell) {
        spell.deliver.type = Spell.Delivery.Type.CLOUD;
        spell.deliver.delay = 10;

        var areaParticle = SpellEngineParticles.area_effect_480;
        var radius = 2;

        var cloud = new Spell.Delivery.Cloud();
        cloud.volume.radius = radius;
        cloud.volume.area.vertical_range_multiplier = 0.3F;
        cloud.volume.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IMPACT_2.id().toString());
        cloud.impact_tick_interval = 8;
        cloud.time_to_live_seconds = 5;
        cloud.spawn.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IGNITE.id().toString());
        cloud.client_data = new Spell.Delivery.Cloud.ClientData();
        cloud.client_data.light_level = 6;
        cloud.client_data.particles = new ParticleBatch[] {
                new ParticleBatch(SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        2, 0.1F, 0.12F)
        };
        cloud.client_data.particle_spawn_interval = SpellEngineParticles.area_effect_480.texture().frames();
        cloud.client_data.interval_particles = new ParticleBatch[] {
                new ParticleBatch(areaParticle.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.GROUND,
                        1, 0.0F, 0.F)
                        .scale(radius)
                        .color(FROST_CLOUD_COLOR.alpha(0.75F).toRGBA()),
        };
        spell.deliver.clouds = List.of(cloud);

        var impact = createEffectImpact(ArsenalEffects.FROSTBITE.id.toString(), 2);
        impact.sound = new Sound(SpellEngineSounds.STUN_GENERIC.id().toString());
        impact.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.snowflake.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        20, 0.05F, 0.15F)
        };
        spell.impacts = List.of(impact);
    }

    public static Color COOLDOWN_SHOT_COLOR = Color.from(0xffccff);
    public static Entry cooldown_shot_spell = add(cooldown_shot_spell());
    private static Entry cooldown_shot_spell() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "cooldown_shot_spell");
        var title = "Cooldown Shot";
        var description = "On spell critical hit: {trigger_chance} chance to reset your spell cooldowns.";

        var spell = passiveSpellBase();
        spell.school = SpellSchools.ARCANE;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        trigger.impact.critical = true;
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.type = Spell.Type.ACTIVE;
        trigger.chance = 0.5F;
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var impact = new Spell.Impact();
        impact.action = new Spell.Impact.Action();
        impact.action.type = Spell.Impact.Action.Type.COOLDOWN;
        impact.action.cooldown = new Spell.Impact.Action.Cooldown();
        impact.action.cooldown.actives = new Spell.Impact.Action.Cooldown.Modify();
        impact.action.cooldown.actives.duration_multiplier = 0;
        impact.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.sign_hourglass.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.CENTER,
                        1, 0.75F, 0.75F)
                        .scale(0.8F)
                        .color(COOLDOWN_SHOT_COLOR.toRGBA())
                        .followEntity(true),
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        40, 0.3F, 0.3F)
                        .color(COOLDOWN_SHOT_COLOR.toRGBA())
        };
        impact.sound = new Sound(ArsenalSounds.spell_cooldown_impact.id().toString());
        spell.impacts = List.of(impact);

        configureCooldown(spell, 30);

        return new Entry(id, spell, title, description, null, EnumSet.of(Category.SPELL, Category.HEAL));
    }

    public static final Color SHOCKWAVE_COLOR = Color.from(0xa7e5f5);
    public static Entry shockwave_melee = add(shockwave_melee());
    private static Entry shockwave_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "shockwave_melee");
        var title = "Shockwave";
        var description = "The last attack in a combo sends a shockwave forward, dealing {damage} damage to enemies in its path.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;
        spell.range = 10F;

//        var trigger = new Spell.Trigger();
//        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
//        trigger.equipment_condition = EquipmentSlot.MAINHAND;
//        trigger.chance = 0.3F;
//        trigger.chance_batching = true;

        var trigger = SpellBuilder.Triggers.withConditionMustWield(
                SpellBuilder.Triggers.meleeAttackImpact()
        );
        trigger.melee = new Spell.Trigger.MeleeCondition();
        trigger.melee.is_combo = true;
        trigger.melee.is_offhand = false;

        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.inherit_shooter_pitch = false;
        spell.deliver.projectile.launch_properties.velocity = 0.75F;
        spell.deliver.projectile.launch_properties.sound = new Sound(ArsenalSounds.shockwave_release.id().toString());
        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 0F;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = ArsenalProjectiles.shockwave_large.id().toString();

        projectile.client_data.model.scale = 4F;
        projectile.client_data.model.rotate_degrees_per_tick = 0;
        projectile.perks.pierce = 999;
        projectile.hitbox = new Spell.ProjectileData.HitBox(3.5F, 0.5F);
        spell.deliver.projectile.projectile = projectile;

        var damage = damageImpact(0.25F, 0.5F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.BURST).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        null, 20, 0.2F, 0.7F, 0.0F, 0F)
                        .color(SHOCKWAVE_COLOR.toRGBA())
        };
        damage.sound = new Sound(ArsenalSounds.shockwave_impact.id().toString());
        spell.impacts = List.of(damage);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry shockwave_area_spell = add(shockwave_area_spell());
    private static Entry shockwave_area_spell() {
        var cooldown_threshold = 5;
        var id = Identifier.of(ArsenalMod.NAMESPACE, "shockwave_area_spell");
        var title = "Shockwave Area";
        var description = "Damaging spells with longer than " + cooldown_threshold + " seconds cooldown, send shockwaves around you, dealing {damage} damage to enemies in their path.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.ARCANE;
        spell.range = 10F;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        // trigger.chance = 1F;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.cooldown_min = cooldown_threshold;
        trigger.spell.type = Spell.Type.ACTIVE;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.direct_towards_target = true;
        spell.deliver.projectile.launch_properties.velocity = 0.6F;
        spell.deliver.projectile.launch_properties.extra_launch_count = 3;
        spell.deliver.projectile.launch_properties.extra_launch_delay = 0;
        spell.deliver.projectile.launch_properties.sound = Sound.withVolume(ArsenalSounds.shockwave_release.id(), 0.6F);
        spell.deliver.projectile.direction_offsets = new Spell.Delivery.ShootProjectile.DirectionOffset[] {
                new Spell.Delivery.ShootProjectile.DirectionOffset(0, 0),
                new Spell.Delivery.ShootProjectile.DirectionOffset(90, 0),
                new Spell.Delivery.ShootProjectile.DirectionOffset(180, 0),
                new Spell.Delivery.ShootProjectile.DirectionOffset(270, 0)
        };
        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 0F;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = ArsenalProjectiles.shockwave.id().toString();

        projectile.client_data.model.scale = 2F;
        projectile.client_data.model.rotate_degrees_per_tick = 0;
        projectile.perks.pierce = 999;
        projectile.hitbox = new Spell.ProjectileData.HitBox(2F, 0.4F);

        spell.deliver.projectile.projectile = projectile;

        var damage = damageImpact(0.25F, 0.5F);
        damage.action.min_power = 7;
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.BURST).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        null, 20, 0.2F, 0.7F, 0.0F, 0F)
                        .color(SHOCKWAVE_COLOR.toRGBA())
        };
        damage.sound = new Sound(ArsenalSounds.shockwave_impact.id().toString());
        spell.impacts = List.of(damage);

        configureCooldown(spell, 4);

        return new Entry(id, spell, title, description, null, EnumSet.of(Category.SPELL, Category.HEAL));
    }

    public static final Color CHAIN_REACTION_COLOR = Color.from(0xe4dfff);
    public static Entry chain_reaction_spell = add(chain_reaction_spell());
    private static Entry chain_reaction_spell() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "chain_reaction_spell");
        var title = "Chain Reaction";
        var description = "On spell critical hit: launches a spell projectile with chain reaction, dealing {damage} spell damage.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.ARCANE;
        spell.range = 20F;

        var trigger = new Spell.Trigger();
        // trigger.chance = 0.5F;
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        trigger.impact.critical = true;
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.type = Spell.Type.ACTIVE;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.direct_towards_target = true;
        spell.deliver.projectile.launch_properties.velocity = 0.75F;
        spell.deliver.projectile.launch_properties.sound = new Sound(ArsenalSounds.missile_release.id().toString());
        spell.deliver.projectile.direction_offsets = new Spell.Delivery.ShootProjectile.DirectionOffset[] {
                new Spell.Delivery.ShootProjectile.DirectionOffset(0, -80)
        };
        var projectile = new Spell.ProjectileData();
        projectile.homing_angles = new float[] { 10, 20, 30, 20F };
        projectile.homing_angle = 3F;
        projectile.perks.chain_reaction_size = 3;
        projectile.perks.chain_reaction_triggers = 1;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 10;
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SPELL_ASCEND.toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK, 1, 0.05F, 0.1F, 0.0F, 0F)
                        .color(CHAIN_REACTION_COLOR.toRGBA())
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = ArsenalProjectiles.missile.id().toString();
        projectile.client_data.model.scale = 0.5F;
        spell.deliver.projectile.projectile = projectile;

        var damage = damageImpact(0.5F, 0.25F);
        damage.action.min_power = 7;
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.BURST).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        null, 20, 0.2F, 0.7F, 0.0F, 0F)
                        .color(CHAIN_REACTION_COLOR.toRGBA())
        };
        damage.sound = new Sound(ArsenalSounds.missile_impact.id().toString());
        spell.impacts = List.of(damage);

        configureCooldown(spell,  1);

        return new Entry(id, spell, title, description, null, EnumSet.of(Category.SPELL, Category.HEAL));
    }

    public static Entry guardian_heal = add(guardian_heal());
    private static Entry guardian_heal() {
        var threshold = 0.5F;
        var duration = 6;

        var id = Identifier.of(ArsenalMod.NAMESPACE, "guardian_heal");
        var title = "Guardian Remedy";
        var description = "Healing targets under {threshold} health grants them a temporary absorption shield, lasting {effect_duration} seconds.";
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            return args.description().replace("{threshold}", SpellTooltip.percent(threshold));
        };
        var spell = passiveSpellBase();
        spell.school = SpellSchools.HEALING;

        var trigger = new Spell.Trigger();
        trigger.stage = Spell.Trigger.Stage.PRE;
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.HEAL.toString();
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.type = Spell.Type.ACTIVE;
        trigger.target_conditions = List.of(weakCondition());
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var effect = createEffectImpact(ArsenalEffects.ABSORPTION.id.toString(), duration);
        effect.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        effect.action.status_effect.amplifier_power_multiplier = 0.2F;
        effect.particles = new ParticleBatch[]{
                new ParticleBatch(
                        SpellEngineParticles.area_circle_1.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.FEET,
                        1, 0.2F, 0.2F)
                        .followEntity(true)
                        .scale(0.8F)
                        .maxAge(0.4F)
                        .color(Color.HOLY.toRGBA()),
                new ParticleBatch(SpellEngineParticles.area_effect_714.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.GROUND,
                        1, 0.0F, 0.F)
                        .color(Color.HOLY.toRGBA())
        };
        effect.sound = new Sound(ArsenalSounds.guardian_heal_impact.id().toString());
        spell.impacts = List.of(effect);

        configureCooldown(spell, duration);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, mutator, Category.HEAL);
    }

    public static final Color COOLDOWN_HEAL_COLOR = Color.from(0xffcc99);
    public static Entry cooldown_heal = add(cooldown_heal());
    private static Entry cooldown_heal() {
        var threshold = 0.5F;

        var id = Identifier.of(ArsenalMod.NAMESPACE, "cooldown_heal");
        var title = "Cooldown Touch";
        var description = "Healing targets under {threshold} health, has {trigger_chance} chance to reset your spell cooldowns.";
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            return args.description().replace("{threshold}", SpellTooltip.percent(threshold));
        };
        var spell = passiveSpellBase();
        spell.school = SpellSchools.HEALING;

        var trigger = new Spell.Trigger();
        trigger.stage = Spell.Trigger.Stage.PRE;
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.HEAL.toString();
        trigger.target_conditions = List.of(weakCondition());
        trigger.chance = 0.5F;
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        spell.deliver.delay = 1;

        var impact = new Spell.Impact();
        impact.action = new Spell.Impact.Action();
        impact.action.type = Spell.Impact.Action.Type.COOLDOWN;
        impact.action.cooldown = new Spell.Impact.Action.Cooldown();
        impact.action.cooldown.actives = new Spell.Impact.Action.Cooldown.Modify();
        impact.action.cooldown.actives.duration_multiplier = 0;
        impact.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.sign_hourglass.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.CENTER,
                        1, 0.75F, 0.75F)
                        .scale(0.8F)
                        .color(COOLDOWN_HEAL_COLOR.toRGBA())
                        .followEntity(true),
                new ParticleBatch(SPARK_DECELERATE.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        40, 0.3F, 0.3F)
                        .color(COOLDOWN_HEAL_COLOR.toRGBA())
        };
        impact.sound = new Sound(ArsenalSounds.spell_cooldown_impact.id().toString());
        spell.impacts = List.of(impact);

        configureCooldown(spell, 30);

        return new Entry(id, spell, title, description, mutator, Category.HEAL);
    }
}