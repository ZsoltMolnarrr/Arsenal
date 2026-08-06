package net.arsenal.spell;

import net.arsenal.ArsenalMod;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.Fx;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder.Batches;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.spell.tooltip.TooltipTokens;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
import net.spell_power.api.SpellSchools;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public class ArsenalSpells {
    public enum Category {
        MELEE, RANGED, SPELL, HEAL, SHIELD
    }
    public record Entry(Identifier id, Spell spell, String title, String description,
                        EnumSet<Category> categories) {
        public Entry(Identifier id, Spell spell, String title, String description, Category category) {
            this(id, spell, title, description, EnumSet.of(category));
        }
    }

    public static final List<Entry> all = new ArrayList<>();
    private static Entry add(Entry entry) {
        all.add(entry);
        return entry;
    }

    /// A percentage baked directly into a description literal. The description is a lang value and
    /// `I18n.translate` feeds it to `String.format`, so a literal `%` must be doubled (`%%` → `%`) or
    /// it renders as "Format error". Token percentages injected after translation don't need this.
    private static String bakedPercent(float value) {
        return TooltipTokens.percent(value).replace("%", "%%");
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

    // MARK: Particle shorthands
    // The `magic_<shape>_<motion>` ids collapsed to eight `magic_<shape>` entries, with motion
    // chosen per effect — so what used to be an id constant is now a half-built effect.

    private static ParticleGroupBuilder holyDecelerate() {
        return ParticleGroupBuilder.magic(SpellEngineParticles.magic_holy, ParticleGroup.Motion.DECELERATE);
    }
    private static ParticleGroupBuilder sparkDecelerate() {
        return ParticleGroupBuilder.magic(SpellEngineParticles.magic_spark, ParticleGroup.Motion.DECELERATE);
    }
    private static ParticleGroupBuilder sparkFloat() {
        return ParticleGroupBuilder.magic(SpellEngineParticles.magic_spark, ParticleGroup.Motion.FLOAT);
    }
    private static ParticleGroupBuilder stripeFloat() {
        return ParticleGroupBuilder.magic(SpellEngineParticles.magic_stripe, ParticleGroup.Motion.FLOAT);
    }
    private static ParticleGroupBuilder spellAscend() {
        return ParticleGroupBuilder.magic(SpellEngineParticles.magic_spell, ParticleGroup.Motion.ASCEND);
    }
    private static ParticleGroupBuilder skullDecelerate() {
        return ParticleGroupBuilder.magic(SpellEngineParticles.magic_skull, ParticleGroup.Motion.DECELERATE);
    }
    private static ParticleGroupBuilder arcaneBurst() {
        return ParticleGroupBuilder.magic(SpellEngineParticles.magic_arcane, ParticleGroup.Motion.BURST);
    }

    /// Outward from the target's centre — the standard Arsenal impact spray.
    private static Consumer<ParticleGroup.Batch> burst(float count, float minSpeed, float maxSpeed) {
        return b -> b.shape(ParticleGroup.Shape.SPHERE).count(count).speed(minSpeed, maxSpeed);
    }

    /// A column rising from the feet.
    private static Consumer<ParticleGroup.Batch> column(float count, float minSpeed, float maxSpeed) {
        return b -> b.shape(ParticleGroup.Shape.PILLAR).count(count)
                .speed(minSpeed, maxSpeed).verticalOrigin(Batches.FEET);
    }

    /// A sign icon riding on the entity — Arsenal's buff and cooldown markers.
    private static ParticleGroup sign(SpellEngineParticles.Entry entry, long color) {
        return ParticleGroupBuilder.of(entry).scale(0.8F).color(color).attached()
                .batch(b -> b.shape(ParticleGroup.Shape.LINE_VERTICAL).count(1).speed(0.75F, 0.75F));
    }

    /// The flat ring pulsed under a buffed entity. `playbackSpeed` replaces V1's `maxAge` lifetime
    /// multiplier and is its **reciprocal** — V1 `maxAge(0.4)` is `playbackSpeed(2.5)`.
    private static ParticleGroup ringDecal(float playbackSpeed, long color) {
        return ParticleGroupBuilder.of(SpellEngineParticles.area_circle_1)
                .attached().scale(0.8F).playbackSpeed(playbackSpeed).color(color)
                .batch(b -> b.shape(ParticleGroup.Shape.LINE_VERTICAL).count(1)
                        .speed(0.2F, 0.2F).verticalOrigin(Batches.FEET));
    }

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
        // Ground decal sized to the spell's reach. `scale_with` multiplies the authored scale
        // (the old `particles_scaled_with_ranged` replaced it), so scale stays at its default 1.
        spell.release.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.zone(particleId)
                        .color(particleColor)
                        .scaleWith(Fx.ScaleWith.RANGE)
                        .batch(Batches.ground(1))
        );

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

        return new Entry(id, spell, title, description, Category.MELEE);
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

        return new Entry(id, spell, title, description, Category.RANGED);
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

        return new Entry(id, spell, title, description, EnumSet.of(Category.SPELL, Category.HEAL));
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
        heal.visuals = Fx.Visuals.of(
                sparkDecelerate().color(HOLY_COLOR)
                        .batch(b -> b.shape(ParticleGroup.Shape.PIPE).widthFactor(2F)
                                .count(20).speed(0.1F, 0.1F).verticalOrigin(Batches.FEET)),
                ringDecal(1.25F, Color.HOLY.toRGBA()),
                holyDecelerate().color(HOLY_COLOR).batch(burst(15, 0.2F, 0.25F))
        );
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

        return new Entry(id, spell, title, description, Category.MELEE);
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
        spell.area_impact.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(SpellEngineParticles.fire_explosion)
                        .batch(burst(1, 0F, 0.1F))
        );
        spell.area_impact.sound = new Sound(SpellEngineSounds.GENERIC_FIRE_IMPACT_1.id().toString());

        return new Entry(id, spell, title, description, Category.MELEE);
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

        return new Entry(id, spell, title, description, Category.MELEE);
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

        return new Entry(id, spell, title, description, Category.RANGED);
    }

    private static void witherImpact(Spell spell, float amplifier_multiplier) {
        var wither = createEffectImpact("wither", 5);
        wither.action.status_effect.amplifier_power_multiplier = amplifier_multiplier;
        wither.action.status_effect.show_particles = true;
        wither.visuals = Fx.Visuals.of(
                skullDecelerate().color(WITHER_COLOR).batch(burst(25, 0.2F, 0.25F))
        );
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

        return new Entry(id, spell, title, description, Category.MELEE);
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

        return new Entry(id, spell, title, description, Category.RANGED);
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

        return new Entry(id, spell, title, description, Category.SPELL);
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
        cloud.client_data.particles = List.of(
                ParticleGroupBuilder.of(SpellEngineParticles.flame_ground).batch(column(3, 0, 0)),
                ParticleGroupBuilder.of(SpellEngineParticles.flame_medium_a).batch(column(2, 0.02F, 0.1F)),
                ParticleGroupBuilder.of(SpellEngineParticles.flame_medium_b).batch(column(1, 0.02F, 0.1F)),
                ParticleGroupBuilder.of(SpellEngineParticles.flame_spark).batch(column(3, 0.03F, 0.2F))
        );
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
        damage.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(SpellEngineParticles.flame).batch(column(20, 0.05F, 0.15F)),
                ParticleGroupBuilder.of(SpellEngineParticles.flame_medium_a).batch(column(20, 0.05F, 0.15F))
        );
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

        return new Entry(id, spell, title, description, Category.MELEE);
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

        return new Entry(id, spell, title, description, Category.RANGED);
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
        cloud.client_data.particles = List.of(
                ParticleGroupBuilder.of(SpellEngineParticles.smoke_large)
                        .color(0x99FF66AAL).batch(column(1, 0.01F, 0.02F)),
                ParticleGroupBuilder.of(SpellEngineParticles.smoke_large)
                        .color(0x33DD33EE).batch(column(1, 0.01F, 0.02F))
        );
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
        impact.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(SpellEngineParticles.smoke_large)
                        .color(0x33DD33AAL).batch(burst(0.5F, 0.01F, 0.02F)),
                skullDecelerate().color(0x33DD33AAL).batch(burst(3, 0.1F, 0.2F))
        );
        spell.impacts = List.of(impact);
    }

    public static Entry slowing_melee = add(slowing_melee());
    private static Entry slowing_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "slowing_melee");
        var title = "Frostbite";
        // Frostbite carries two equal modifiers (movement + attack speed); name one explicitly since
        // the "sole modifier" default is only deterministic for single-modifier effects.
        var description = "On melee hit: {trigger_chance} chance to slow movement and attack speed of the the target by "
                + TooltipTokens.effect(ArsenalEffects.FROSTBITE.id, 0,
                        Identifier.of(EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString()),
                        TooltipTokens.Format.ABS)
                + ", for {effect_duration} seconds.";

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
        slow.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(SpellEngineParticles.snowflake).batch(burst(25, 0.1F, 0.15F))
        );
        slow.sound = new Sound(SpellEngineSounds.STUN_GENERIC.id().toString());
        spell.impacts = List.of(slow);

        configureCooldown(spell, 3);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, Category.MELEE);
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

        return new Entry(id, spell, title, description, Category.MELEE);
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

        return new Entry(id, spell, title, description, Category.SPELL);
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
        leech.visuals = Fx.Visuals.of(
                sparkFloat().color(LEECHING_COLOR)
                        .batch(b -> b.shape(ParticleGroup.Shape.PIPE).widthFactor(2F)
                                .count(15).speed(0.02F, 0.1F)),
                sparkDecelerate().color(LEECHING_COLOR).attached()
                        .batch(burst(25, 0.08F, 0.12F).andThen(b -> b.invert(true).preTravel(5))),
                ParticleGroupBuilder.of(SpellEngineParticles.ground_glow)
                        .attachedToGround().scale(0.8F).color(LEECHING_COLOR.alpha(0.2F))
                        .batch(b -> b.shape(ParticleGroup.Shape.LINE_VERTICAL).count(1)
                                .anchor(ParticleGroup.Anchor.GROUND))
        );
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

        // Sized to the swing's reach. The authored `scale(0.8)` was dead code under the old
        // range path (which replaced scale outright), so it is dropped rather than carried over.
        spell.release.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(SpellEngineParticles.area_swirl)
                        .attached()
                        .scaleWith(Fx.ScaleWith.RANGE)
                        .batch(Batches.placed(1))
        );

        var damage = damageImpact(0.5F, 0.5F);
        spell.impacts = List.of(damage);

        return new Entry(id, spell, title, description, Category.MELEE);
    }

    public static final Color GUARDING_COLOR = Color.from(0x66ccff);
    public static Entry guarding_strike_melee = add(guarding_strike_melee());
    private static Entry guarding_strike_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "guarding_strike_melee");
        var title = "Guarding Strike";
        var description = "Defeating enemies grants you and nearby allies a temporary effect reducing damage taken by "
                + TooltipTokens.effect(ArsenalEffects.GUARDING.id, 0, null, TooltipTokens.Format.ABS)
                + ", lasting {effect_duration} seconds.";

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
        buff.visuals = Fx.Visuals.of(
                ringDecal(2.5F, GUARDING_COLOR.toRGBA()),
                sign(SpellEngineParticles.sign_shield, GUARDING_COLOR.alpha(0.75F).toRGBA())
        );
        buff.sound = new Sound(ArsenalSounds.guardian_strike_impact.id().toString());
        spell.impacts = List.of(buff);
        configureCooldown(spell, 10);

        return new Entry(id, spell, title, description, Category.MELEE);
    }

    public static Color SUNDERING_COLOR = Color.from(0x595959);
    public static Entry sundering_melee = add(sundering_melee());
    private static Entry sundering_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "sundering_melee");
        var title = "Sundering";
        var description = "On melee hit: {trigger_chance} chance to reduce the target's armor by "
                + TooltipTokens.effect(ArsenalEffects.SUNDERING.id, 0, null, TooltipTokens.Format.ABS)
                + " for {effect_duration} seconds.";

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
        sunder.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(SpellEngineParticles.smoke_medium)
                        .color(SUNDERING_COLOR).batch(burst(25, 0.1F, 0.1F))
        );
        sunder.sound = new Sound(ArsenalSounds.sunder_impact.id().toString());
        spell.impacts = List.of(sunder);

        configureCooldown(spell, 5);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, Category.MELEE);
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
        buff.visuals = Fx.Visuals.of(
                sign(SpellEngineParticles.sign_shield, UNYIELDING_COLOR.alpha(0.75F).toRGBA()),
                sparkDecelerate().color(UNYIELDING_COLOR).batch(burst(10, 0.3F, 0.35F))
        );
        buff.sound = new Sound(ArsenalSounds.unyielding_impact.id().toString());
        spell.impacts = List.of(buff);

        configureCooldown(spell, duration * 2);

        return new Entry(id, spell, title, description, Category.SHIELD);
    }

    public static Entry guarding_shield = add(guarding_shield());
    private static Entry guarding_shield() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "guarding_shield");
        var title = "Guarding";
        var description = "On shield block: {trigger_chance} chance to reduce damage taken by "
                + TooltipTokens.effect(ArsenalEffects.GUARDING.id, 0, null, TooltipTokens.Format.ABS)
                + ", lasting {effect_duration} seconds.";

        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var trigger = SpellBuilder.Triggers.shieldBlock();
        trigger.chance = 0.3F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        buffAreaTarget(spell, SpellEngineParticles.area_effect_714.id(), GUARDING_COLOR.toRGBA());

        var buff = createEffectImpact(ArsenalEffects.GUARDING.id.toString(), 5);
        buff.visuals = Fx.Visuals.of(
                ringDecal(2.5F, GUARDING_COLOR.toRGBA()),
                sign(SpellEngineParticles.sign_shield, GUARDING_COLOR.alpha(0.75F).toRGBA())
        );
        buff.sound = new Sound(ArsenalSounds.guardian_strike_impact.id().toString());
        spell.impacts = List.of(buff);
        configureCooldown(spell, 10);

        return new Entry(id, spell, title, description, Category.SHIELD);
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
        damage.visuals = Fx.Visuals.of(
                sparkFloat().color(SPIKED_COLOR).batch(burst(10, 0.3F, 0.35F))
        );
        damage.sound = new Sound(ArsenalSounds.spike_impact.id().toString());

        spell.impacts = List.of(damage);

        return new Entry(id, spell, title, description, Category.SHIELD);
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

        spell.release.visuals = Fx.Visuals.of(
                sparkDecelerate()
                        .batch(b -> b.shape(ParticleGroup.Shape.PIPE).count(25).speed(0.2F, 0.7F)
                                .anchor(ParticleGroup.Anchor.LAUNCH_POINT)
                                .alignment(ParticleGroup.Alignment.LOOK))
        );

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

        return new Entry(id, spell, title, description, Category.RANGED);
    }

    private static final float RAMPAGING_DURATION = 12;
    private static final float RAMPAGING_COOLDOWN = 20;

    public static Color RAMPAGING_COLOR = Color.from(0xff471a);
    public static Entry rampaging_melee = add(rampaging_melee());
    private static Entry rampaging_melee() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "rampaging_melee");
        var title = "Rampaging";
        var effect = ArsenalEffects.RAMPAGING;
        var description = "Defeating enemies grants " + title + " effect, increasing your damage by "
                + TooltipTokens.effect(effect.id, 0, null, TooltipTokens.Format.ABS)
                + ", stacking up to {effect_amplifier_cap} times, lasting {effect_duration} seconds.";

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
        buff.visuals = Fx.Visuals.of(
                sparkDecelerate().color(RAMPAGING_COLOR).batch(burst(10, 0.3F, 0.35F))
        );
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.amplifier = 1;
        buff.action.status_effect.amplifier_cap = 4;
        buff.action.status_effect.refresh_duration = false;

        spell.impacts = List.of(buff);

        configureCooldown(spell, RAMPAGING_COOLDOWN);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, Category.MELEE);
    }

    public static Color FOCUSING_COLOR = Color.from(0x99ff66);
    public static Entry rampaging_ranged = add(rampaging_ranged());
    private static Entry rampaging_ranged() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "rampaging_ranged");
        var effect = ArsenalEffects.FOCUSING;
        var title = "Focusing";
        var description = "Defeating enemies grants " + effect.title + " effect, increasing your damage by "
                + TooltipTokens.effect(effect.id, 0, null, TooltipTokens.Format.ABS)
                + ", stacking up to {effect_amplifier_cap} times, lasting {effect_duration} seconds.";

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
        buff.visuals = Fx.Visuals.of(
                sparkDecelerate().color(FOCUSING_COLOR).batch(burst(10, 0.3F, 0.35F))
        );
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.amplifier = 1;
        buff.action.status_effect.amplifier_cap = 2;
        buff.action.status_effect.refresh_duration = false;

        spell.impacts = List.of(buff);

        configureCooldown(spell, RAMPAGING_COOLDOWN);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, Category.RANGED);
    }

    public static final Color SURGING_COLOR = Color.from(0x99ffff);
    public static Entry rampaging_spell = add(rampaging_spell());
    private static Entry rampaging_spell() {
        var id = Identifier.of(ArsenalMod.NAMESPACE, "rampaging_spell");
        var effect = ArsenalEffects.SURGING;
        var title = "Surging";
        var description = "Defeating enemies grants " + effect.title + " effect, increasing your spell critical chance by "
                + TooltipTokens.effect(effect.id, 0, null, TooltipTokens.Format.ABS)
                + ", stacking up to {effect_amplifier_cap} times, lasting {effect_duration} seconds.";

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
        buff.visuals = Fx.Visuals.of(
                sparkDecelerate().color(SURGING_COLOR).batch(burst(10, 0.3F, 0.35F))
        );
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.amplifier = 1;
        buff.action.status_effect.amplifier_cap = 4;
        buff.action.status_effect.refresh_duration = false;

        spell.impacts = List.of(buff);

        configureCooldown(spell, RAMPAGING_COOLDOWN);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, Category.SPELL);
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

        return new Entry(id, spell, title, description, Category.SPELL);
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
        cloud.client_data.particles = List.of(
                ParticleGroupBuilder.of(SpellEngineParticles.snowflake).batch(column(2, 0.1F, 0.12F))
        );
        cloud.client_data.particle_spawn_interval = SpellEngineParticles.area_effect_480.texture().frames();
        cloud.client_data.interval_particles = List.of(
                ParticleGroupBuilder.zone(areaParticle)
                        .scale(radius)
                        .color(FROST_CLOUD_COLOR.alpha(0.75F))
                        .batch(Batches.ground(1))
        );
        spell.deliver.clouds = List.of(cloud);

        var impact = createEffectImpact(ArsenalEffects.FROSTBITE.id.toString(), 2);
        impact.sound = new Sound(SpellEngineSounds.STUN_GENERIC.id().toString());
        impact.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(SpellEngineParticles.snowflake).batch(column(20, 0.05F, 0.15F))
        );
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
        impact.visuals = Fx.Visuals.of(
                sign(SpellEngineParticles.sign_hourglass, COOLDOWN_SHOT_COLOR.toRGBA()),
                sparkDecelerate().color(COOLDOWN_SHOT_COLOR).batch(burst(40, 0.3F, 0.3F))
        );
        impact.sound = new Sound(ArsenalSounds.spell_cooldown_impact.id().toString());
        spell.impacts = List.of(impact);

        configureCooldown(spell, 30);

        return new Entry(id, spell, title, description, EnumSet.of(Category.SPELL, Category.HEAL));
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
        var shockwaveLargeModel = SpellBuilder.ProjectileModels.model(ArsenalProjectiles.shockwave_large.id().toString(), 4F);
        shockwaveLargeModel.rotate_degrees_per_tick = 0;
        projectile.client_data.composite_model = SpellBuilder.ProjectileModels.composite(shockwaveLargeModel);
        projectile.perks.pierce = 999;
        projectile.hitbox = new Spell.ProjectileData.HitBox(3.5F, 0.5F);
        projectile.hitbox.length = 1F; // thin wall along travel; without this the OBB depth falls back to width (3.5)
        spell.deliver.projectile.projectile = projectile;

        var damage = damageImpact(0.25F, 0.5F);
        damage.visuals = Fx.Visuals.of(
                arcaneBurst().color(SHOCKWAVE_COLOR).batch(burst(20, 0.2F, 0.7F))
        );
        damage.sound = new Sound(ArsenalSounds.shockwave_impact.id().toString());
        spell.impacts = List.of(damage);

        return new Entry(id, spell, title, description, Category.MELEE);
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
        var shockwaveModel = SpellBuilder.ProjectileModels.model(ArsenalProjectiles.shockwave.id().toString(), 2F);
        shockwaveModel.rotate_degrees_per_tick = 0;
        projectile.client_data.composite_model = SpellBuilder.ProjectileModels.composite(shockwaveModel);
        projectile.perks.pierce = 999;
        projectile.hitbox = new Spell.ProjectileData.HitBox(2F, 0.4F);
        projectile.hitbox.length = 1F; // thin wall along travel; without this the OBB depth falls back to width (2.0)

        spell.deliver.projectile.projectile = projectile;

        var damage = damageImpact(0.25F, 0.5F);
        damage.action.min_power = 7;
        damage.visuals = Fx.Visuals.of(
                arcaneBurst().color(SHOCKWAVE_COLOR).batch(burst(20, 0.2F, 0.7F))
        );
        damage.sound = new Sound(ArsenalSounds.shockwave_impact.id().toString());
        spell.impacts = List.of(damage);

        configureCooldown(spell, 4);

        return new Entry(id, spell, title, description, EnumSet.of(Category.SPELL, Category.HEAL));
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
        projectile.client_data.travel_particles = List.of(
                spellAscend().color(CHAIN_REACTION_COLOR)
                        .batch(Batches.travel(1, 0.1F).andThen(b -> b.speed(0.05F, 0.1F)))
        );
        projectile.client_data.composite_model = SpellBuilder.ProjectileModels.single(ArsenalProjectiles.missile.id().toString(), 0.5F);
        spell.deliver.projectile.projectile = projectile;

        var damage = damageImpact(0.5F, 0.25F);
        damage.action.min_power = 7;
        damage.visuals = Fx.Visuals.of(
                arcaneBurst().color(CHAIN_REACTION_COLOR).batch(burst(20, 0.2F, 0.7F))
        );
        damage.sound = new Sound(ArsenalSounds.missile_impact.id().toString());
        spell.impacts = List.of(damage);

        configureCooldown(spell,  1);

        return new Entry(id, spell, title, description, EnumSet.of(Category.SPELL, Category.HEAL));
    }

    public static Entry guardian_heal = add(guardian_heal());
    private static Entry guardian_heal() {
        var threshold = 0.5F;
        var duration = 6;

        var id = Identifier.of(ArsenalMod.NAMESPACE, "guardian_heal");
        var title = "Guardian Remedy";
        var description = "Healing targets under " + bakedPercent(threshold)
                + " health grants them a temporary absorption shield, lasting {effect_duration} seconds.";
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
        effect.visuals = Fx.Visuals.of(
                ringDecal(2.5F, Color.HOLY.toRGBA()),
                ParticleGroupBuilder.zone(SpellEngineParticles.area_effect_714)
                        .color(Color.HOLY)
                        .batch(Batches.ground(1))
        );
        effect.sound = new Sound(ArsenalSounds.guardian_heal_impact.id().toString());
        spell.impacts = List.of(effect);

        configureCooldown(spell, duration);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, Category.HEAL);
    }

    public static final Color COOLDOWN_HEAL_COLOR = Color.from(0xffcc99);
    public static Entry cooldown_heal = add(cooldown_heal());
    private static Entry cooldown_heal() {
        var threshold = 0.5F;

        var id = Identifier.of(ArsenalMod.NAMESPACE, "cooldown_heal");
        var title = "Cooldown Touch";
        var description = "Healing targets under " + bakedPercent(threshold)
                + " health, has {trigger_chance} chance to reset your spell cooldowns.";
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
        impact.visuals = Fx.Visuals.of(
                sign(SpellEngineParticles.sign_hourglass, COOLDOWN_HEAL_COLOR.toRGBA()),
                sparkDecelerate().color(COOLDOWN_HEAL_COLOR).batch(burst(40, 0.3F, 0.3F))
        );
        impact.sound = new Sound(ArsenalSounds.spell_cooldown_impact.id().toString());
        spell.impacts = List.of(impact);

        configureCooldown(spell, 30);

        return new Entry(id, spell, title, description, Category.HEAL);
    }
}