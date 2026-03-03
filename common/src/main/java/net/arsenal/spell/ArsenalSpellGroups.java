package net.arsenal.spell;

import net.arsenal.ArsenalMod;
import net.minecraft.registry.tag.TagKey;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.tags.SpellTags;

public class ArsenalSpellGroups {
    public static TagKey<Spell> STAFF_ARCANE_FIRE = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_arcane_fire");
    public static TagKey<Spell> STAFF_ARCANE_HEALING = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_arcane_healing");
    public static TagKey<Spell> STAFF_ARCANE_FROST = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_arcane_frost");
    public static TagKey<Spell> STAFF_FIRE_FROST = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_fire_frost");
    public static TagKey<Spell> STAFF_ARCANE_FIRE_FROST = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_arcane_fire_frost");
    public static TagKey<Spell> STAFF_HEALING = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_healing");


    public static TagKey<Spell> ONE_HANDED_SLASHER = SpellTags.weapon(ArsenalMod.NAMESPACE, "one_handed_slasher");
    public static TagKey<Spell> TWO_HANDED_SLASHER = SpellTags.weapon(ArsenalMod.NAMESPACE, "two_handed_slasher");
    public static TagKey<Spell> CLAYMORE_DOUBLE_AXE = SpellTags.weapon(ArsenalMod.NAMESPACE, "claymore_double_axe");
    public static TagKey<Spell> CLAYMORE_HAMMER = SpellTags.weapon(ArsenalMod.NAMESPACE, "claymore_hammer");
    public static TagKey<Spell> CLAYMORE_GLAIVE = SpellTags.weapon(ArsenalMod.NAMESPACE, "claymore_glaive");
    public static TagKey<Spell> SPEAR_GLAIVE = SpellTags.weapon(ArsenalMod.NAMESPACE, "spear_glaive");
    public static TagKey<Spell> DOUBLE_AXE_HAMMER = SpellTags.weapon(ArsenalMod.NAMESPACE, "double_axe_hammer");
    public static TagKey<Spell> DAGGER_SICKLE = SpellTags.weapon(ArsenalMod.NAMESPACE, "dagger_sickle");
    public static TagKey<Spell> SICKLE_AXE = SpellTags.weapon(ArsenalMod.NAMESPACE, "sickle_axe");
    public static TagKey<Spell> GLAIVE_DOUBLE_AXE = SpellTags.weapon(ArsenalMod.NAMESPACE, "glaive_double_axe");
    public static TagKey<Spell> MACE_SWORD = SpellTags.weapon(ArsenalMod.NAMESPACE, "mace_sword");
}
