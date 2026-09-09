package net.arsenal.spell;

import net.arsenal.ArsenalMod;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.spell_engine.rpg_series.config.AttributeModifier;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.spell_engine.rpg_series.config.EffectConfig;
import net.spell_engine.api.effect.*;
import net.spell_engine.api.entity.SpellEngineAttributes;
import net.spell_power.api.SpellPowerMechanics;

import java.util.ArrayList;
import java.util.List;

public class ArsenalEffects {
    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    /// 1.20.1's `EntityAttribute` is a plain object with no id accessor (1.21's `getIdAsString()`),
    /// so the registry has to be asked. Safe at class-init: vanilla attributes are registered in
    /// `EntityAttributes.<clinit>`, which this dereference triggers.
    private static String attributeId(EntityAttribute attribute) {
        return Registries.ATTRIBUTE.getId(attribute).toString();
    }

    public static Effects.Entry STUN = add(new Effects.Entry(new Identifier(ArsenalMod.NAMESPACE,"stun"),
            "Stunned",
            "Cannot move or act.",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x888800),
            // PORT (1.20.1): the `generic.jump_strength` attribute does not exist for living entities
            // before 1.21 (only `horse.jump_strength`), so the jump lock-out modifier is dropped.
            // Movement/action suppression is carried entirely by `EntityActionsAllowed.STUN` below.
            new EffectConfig(List.of())
    ));

    public static Effects.Entry FROSTBITE = add(new Effects.Entry(new Identifier(ArsenalMod.NAMESPACE, "frostbite"),
            "Frostbite",
            "Slower movement and attack speed.",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x99ccff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                                    -0.25F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            ),
                            new AttributeModifier(
                                    attributeId(EntityAttributes.GENERIC_ATTACK_SPEED),
                                    -0.25F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry GUARDING = add(new Effects.Entry(new Identifier(ArsenalMod.NAMESPACE, "guarding"),
            "Guarding",
            "Increased defense.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x00ff00),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    SpellEngineAttributes.DAMAGE_TAKEN.id.toString(),
                                    -0.3F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry SUNDERING = add(new Effects.Entry(new Identifier(ArsenalMod.NAMESPACE, "sundering"),
            "Sundering",
            "Reduced defense.",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0xff0000),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    attributeId(EntityAttributes.GENERIC_ARMOR),
                                    -0.3F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry RAMPAGING = add(new Effects.Entry(new Identifier(ArsenalMod.NAMESPACE, "rampaging"),
            "Rampaging",
            "Increased attack damage.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xff9900),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    attributeId(EntityAttributes.GENERIC_ATTACK_DAMAGE),
                                    0.05F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry FOCUSING = add(new Effects.Entry(new Identifier(ArsenalMod.NAMESPACE, "focusing"),
            "Focusing",
            "Increased ranged attack damage.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xff9900),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    EntityAttributes_RangedWeapon.DAMAGE.id.toString(),
                                    0.1F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry UNYIELDING = add(new Effects.Entry(new Identifier(ArsenalMod.NAMESPACE, "unyielding"),
            "Unyielding",
            "Increased knockback resistance and toughness.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x00ff00),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    attributeId(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE),
                                    3F,
                                    EntityAttributeModifier.Operation.ADDITION
                            ),
                            new AttributeModifier(
                                    attributeId(EntityAttributes.GENERIC_ARMOR_TOUGHNESS),
                                    0.5F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry SURGING = add(new Effects.Entry(new Identifier(ArsenalMod.NAMESPACE, "surging"),
            "Surging",
            "Increased spell critical chance.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x00ff00),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    SpellPowerMechanics.CRITICAL_CHANCE.id.toString(),
                                    0.1F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static final Effects.Entry ABSORPTION = add(new Effects.Entry(
            new Identifier(ArsenalMod.NAMESPACE, "absorption"),
            "Absorption",
            "Increases maximum absorption",
            new AbsorptionStatusEffect(StatusEffectCategory.BENEFICIAL, 0xffffcc),
            // PORT (1.20.1): there is no `generic.max_absorption` attribute before 1.21 — absorption is a
            // raw float on the entity. `AbsorptionStatusEffect` grants/removes it directly instead.
            new EffectConfig(List.of())
    ));

    /// Stun's action lock-out plus client synchronization for every Arsenal effect (and vanilla poison,
    /// which the poison-cloud weapon skills apply). Creation only — nothing is registered here, so a
    /// loader that registers the effects itself (Forge) calls this before its registration loop.
    ///
    /// Every call takes the raw {@link Effects.Entry#effect}, never `Entry#entry`, so this does not depend
    /// on registration order and needs no `Effects.linkEntries` beforehand. Idempotent.
    public static void configureEffects() {
        ActionImpairing.configure(STUN.effect, EntityActionsAllowed.STUN);

        for (var entry: entries) {
            Synchronized.configure(entry.effect, true);
        }

        Synchronized.configure(StatusEffects.POISON, true);
    }

    public static void register(ConfigFile.Effects config) {
        configureEffects();
        Effects.register(entries, config.effects);
    }
}
