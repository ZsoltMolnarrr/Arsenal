package net.arsenal.spell;

import net.arsenal.ArsenalMod;
import net.minecraft.registry.tag.TagKey;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.tags.SpellTags;

public class ArsenalWeaponSpellTags {
    public static TagKey<Spell> STAFF_ARCANE_FIRE = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_arcane_fire");
    public static TagKey<Spell> STAFF_ARCANE_HEALING = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_arcane_healing");
    public static TagKey<Spell> STAFF_ARCANE_FROST = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_arcane_frost");
    public static TagKey<Spell> STAFF_FIRE_FROST = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_fire_frost");
    public static TagKey<Spell> STAFF_ARCANE_FIRE_FROST = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_arcane_fire_frost");
    public static TagKey<Spell> STAFF_HEALING = SpellTags.weapon(ArsenalMod.NAMESPACE, "staff_healing");
}
