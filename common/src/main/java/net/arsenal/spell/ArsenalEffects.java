package net.arsenal.spell;

import net.arsenal.ArsenalMod;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.ConfigFile;
import net.spell_engine.api.config.EffectConfig;
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

    public static Effects.Entry STUN = add(new Effects.Entry(Identifier.of(ArsenalMod.NAMESPACE,"stun"),
            "Stunned",
            "Cannot move or act.",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x888800),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_JUMP_STRENGTH.getIdAsString(),
                            0,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
            ))
    ));

    public static Effects.Entry FROSTBITE = add(new Effects.Entry(Identifier.of(ArsenalMod.NAMESPACE, "frostbite"),
            "Frostbite",
            "Slower movement and attack speed.",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x99ccff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                                    -0.25F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            ),
                            new AttributeModifier(
                                    EntityAttributes.GENERIC_ATTACK_SPEED.getIdAsString(),
                                    -0.25F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry GUARDING = add(new Effects.Entry(Identifier.of(ArsenalMod.NAMESPACE, "guarding"),
            "Guarding",
            "Increased defense.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x00ff00),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    SpellEngineAttributes.DAMAGE_TAKEN.id.toString(),
                                    -0.3F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry SUNDERING = add(new Effects.Entry(Identifier.of(ArsenalMod.NAMESPACE, "sundering"),
            "Sundering",
            "Reduced defense.",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0xff0000),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    EntityAttributes.GENERIC_ARMOR.getIdAsString(),
                                    -0.3F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry RAMPAGING = add(new Effects.Entry(Identifier.of(ArsenalMod.NAMESPACE, "rampaging"),
            "Rampaging",
            "Increased attack damage.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xff9900),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString(),
                                    0.05F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry FOCUSING = add(new Effects.Entry(Identifier.of(ArsenalMod.NAMESPACE, "focusing"),
            "Focusing",
            "Increased ranged attack damage.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xff9900),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    EntityAttributes_RangedWeapon.DAMAGE.id.toString(),
                                    0.1F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry UNYIELDING = add(new Effects.Entry(Identifier.of(ArsenalMod.NAMESPACE, "unyielding"),
            "Unyielding",
            "Increased knockback resistance and toughness.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x00ff00),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getIdAsString(),
                                    3F,
                                    EntityAttributeModifier.Operation.ADD_VALUE
                            ),
                            new AttributeModifier(
                                    EntityAttributes.GENERIC_ARMOR_TOUGHNESS.getIdAsString(),
                                    0.5F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry SURGING = add(new Effects.Entry(Identifier.of(ArsenalMod.NAMESPACE, "surging"),
            "Surging",
            "Increased spell critical chance.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x00ff00),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    SpellPowerMechanics.CRITICAL_CHANCE.id.toString(),
                                    0.1F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static final Effects.Entry ABSORPTION = add(new Effects.Entry(
            Identifier.of(ArsenalMod.NAMESPACE, "absorption"),
            "Absorption",
            "Increases maximum absorption",
            new AbsorptionStatusEffect(StatusEffectCategory.BENEFICIAL, 0xffffcc),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MAX_ABSORPTION.getIdAsString(),
                            2,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            ))
    ));

    public static void register(ConfigFile.Effects config) {
        ActionImpairing.configure(STUN.effect, EntityActionsAllowed.STUN);

        for (var entry: entries) {
            Synchronized.configure(entry.effect, true);
        }

        Synchronized.configure(StatusEffects.POISON.value(), true);

        Effects.register(entries, config.effects);
    }
}
