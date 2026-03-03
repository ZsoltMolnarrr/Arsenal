package net.arsenal.item;

import net.arsenal.ArsenalMod;
import net.arsenal.spell.ArsenalSpells;
import net.arsenal.spell.ArsenalSpellGroups;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Rarity;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.api.spell.container.SpellContainers;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_engine.rpg_series.item.Weapons;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArsenalWeapons {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry add(Weapon.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final float TIER_5_SPELL_POWER = 8;

    // MARK: Claymores

    public static final Weapon.Entry unique_claymore_1 = add(Weapons.claymoreWithSkill(ArsenalMod.NAMESPACE, "unique_claymore_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.MAGMA_BLOCK))
            .translatedName("Cataclysm's Edge")
            .withAdditionalSpell(ArsenalSpells.exploding_melee.id().toString())
            .lootTheme(Loot.Theme.FIERY.toString()));
    public static final Weapon.Entry unique_claymore_2 = add(Weapons.claymoreWithSkill(ArsenalMod.NAMESPACE, "unique_claymore_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.IRON_BLOCK))
            .translatedName("Champion's Greatsword")
            .withAdditionalSpell(ArsenalSpells.radiance_melee.id().toString())
            .lootTheme(Loot.Theme.DIVINE.toString()));
    public static final Weapon.Entry unique_claymore_sw = add(Weapons.claymore(ArsenalMod.NAMESPACE, "unique_claymore_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Apolyon, the Soul-Render")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.CLAYMORE_DOUBLE_AXE.id().toString())
            .withAdditionalSpell(ArsenalSpells.rampaging_melee.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Damage Staves

    public static final Weapon.Entry unique_staff_damage_1 = add(Weapons.damageStaff(ArsenalMod.NAMESPACE, "unique_staff_damage_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.AMETHYST_BLOCK), List.of(SpellSchools.ARCANE.id, SpellSchools.FROST.id))
            .translatedName("Nexus Key")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.STAFF_ARCANE_FROST.id().toString())
            .withAdditionalSpell(ArsenalSpells.cooldown_shot_spell.id().toString())
            .lootTheme(Loot.Theme.GENERIC.toString()));
    public static final Weapon.Entry unique_staff_damage_2 = add(Weapons.damageStaff(ArsenalMod.NAMESPACE, "unique_staff_damage_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK), List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id))
            .translatedName("Antonidas's Staff of Rapt Concentration")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.STAFF_ARCANE_FIRE.id().toString())
            .withAdditionalSpell(ArsenalSpells.chain_reaction_spell.id().toString())
            .lootTheme(Loot.Theme.CRYSTAL.toString()));
    public static final Weapon.Entry unique_staff_damage_3 = add(Weapons.damageStaff(ArsenalMod.NAMESPACE, "unique_staff_damage_3", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP), List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id))
            .translatedName("Draconic Battle Staff")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.STAFF_ARCANE_FIRE.id().toString())
            .withAdditionalSpell(ArsenalSpells.flame_cloud_spell.id().toString())
            .lootTheme(Loot.Theme.FIERY.toString()));
    public static final Weapon.Entry unique_staff_damage_4 = add(Weapons.damageStaff(ArsenalMod.NAMESPACE, "unique_staff_damage_4", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP), List.of(SpellSchools.FIRE.id, SpellSchools.FROST.id))
            .translatedName("Gargoyle's Bite")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.STAFF_FIRE_FROST.id().toString())
            .withAdditionalSpell(ArsenalSpells.leeching_spell.id().toString())
            .lootTheme(Loot.Theme.EVIL.toString()));
    public static final Weapon.Entry unique_staff_damage_5 = add(Weapons.damageStaff(ArsenalMod.NAMESPACE, "unique_staff_damage_5", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.AMETHYST_BLOCK), List.of(SpellSchools.ARCANE.id, SpellSchools.FROST.id))
            .translatedName("Mage Lord Cane")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.STAFF_ARCANE_FROST.id().toString())
            .withAdditionalSpell(ArsenalSpells.shockwave_area_spell.id().toString())
            .lootTheme(Loot.Theme.GENERIC.toString()));
    public static final Weapon.Entry unique_staff_damage_6 = add(Weapons.damageStaff(ArsenalMod.NAMESPACE, "unique_staff_damage_6", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP), List.of(SpellSchools.ARCANE.id, SpellSchools.FROST.id))
            .translatedName("Endless Winter")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.STAFF_ARCANE_FROST.id().toString())
            .withAdditionalSpell(ArsenalSpells.frost_cloud_spell.id().toString())
            .lootTheme(Loot.Theme.FROSTY.toString()));
    public static final Weapon.Entry unique_staff_damage_sw = add(Weapons.damageStaff(ArsenalMod.NAMESPACE, "unique_staff_damage_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.DIAMOND), List.of(SpellSchools.ARCANE.id, SpellSchools.FIRE.id, SpellSchools.FROST.id))
            .translatedName("Grand Magister's Staff of Torrents")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.STAFF_ARCANE_FIRE_FROST.id().toString())
            .withAdditionalSpell(ArsenalSpells.rampaging_spell.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Healing Staves

    public static final Weapon.Entry unique_staff_heal_1 = add(Weapons.healingStaff(ArsenalMod.NAMESPACE, "unique_staff_heal_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.AMETHYST_BLOCK))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, TIER_5_SPELL_POWER))
            .translatedName("Crystalline Life-Staff")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.STAFF_ARCANE_HEALING.id().toString())
            .withAdditionalSpell(ArsenalSpells.radiance_spell.id().toString())
            .lootTheme(Loot.Theme.CRYSTAL.toString()));
    public static final Weapon.Entry unique_staff_heal_2 = add(Weapons.healingStaff(ArsenalMod.NAMESPACE, "unique_staff_heal_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.IRON_BLOCK))
            .translatedName("Staff of Immaculate Recovery")
            .spellContainer(SpellContainers.forMagicWeapon().withSpell("paladins:holy_shock"))
//            .spellContainer(SpellContainers.forMagicWeapon())
//            .withSpellChoices(ArsenalWeaponSpellTags.STAFF_HEALING.id().toString())
            .withAdditionalSpell(ArsenalSpells.guardian_heal.id().toString())
            .lootTheme(Loot.Theme.DIVINE.toString()));
    public static final Weapon.Entry unique_staff_heal_sw = add(Weapons.healingStaff(ArsenalMod.NAMESPACE, "unique_staff_heal_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Golden Staff of the Sin'dorei")
            .spellContainer(SpellContainers.forMagicWeapon().withSpell("paladins:holy_shock"))
//            .spellContainer(SpellContainers.forMagicWeapon())
//            .withSpellChoices(ArsenalWeaponSpellTags.STAFF_HEALING.id().toString())
            .withAdditionalSpell(ArsenalSpells.cooldown_heal.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Spears

    public static final Weapon.Entry unique_spear_1 = add(Weapons.spearWithSkill(ArsenalMod.NAMESPACE, "unique_spear_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP))
            .translatedName("Sonic Spear")
            .withAdditionalSpell(ArsenalSpells.slowing_melee.id().toString())
            .lootTheme(Loot.Theme.GENERIC.toString()));
    public static final Weapon.Entry unique_spear_2 = add(Weapons.spearWithSkill(ArsenalMod.NAMESPACE, "unique_spear_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Spear of the Damned")
            .withAdditionalSpell(ArsenalSpells.stunning_melee.id().toString())
            .lootTheme(Loot.Theme.DIVINE.toString()));
    public static final Weapon.Entry unique_spear_sw = add(Weapons.spearWithSkill(ArsenalMod.NAMESPACE, "unique_spear_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Mounting Vengeance")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.SPEAR_GLAIVE.id().toString())
            .withAdditionalSpell(ArsenalSpells.leeching_melee.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Daggers

    public static final Weapon.Entry unique_dagger_1 = add(Weapons.daggerWithSkill(ArsenalMod.NAMESPACE, "unique_dagger_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.PRISMARINE))
            .translatedName("Frost Fang")
            .withAdditionalSpell(ArsenalSpells.slowing_melee.id().toString())
            .lootTheme(Loot.Theme.FROSTY.toString()));
    public static final Weapon.Entry unique_dagger_2 = add(Weapons.daggerWithSkill(ArsenalMod.NAMESPACE, "unique_dagger_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP))
            .translatedName("Demonic Shiv")
            .withAdditionalSpell(ArsenalSpells.leeching_melee.id().toString())
            .lootTheme(Loot.Theme.EVIL.toString()));
    public static final Weapon.Entry unique_dagger_sw = add(Weapons.daggerWithSkill(ArsenalMod.NAMESPACE, "unique_dagger_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Crux of the Apocalypse")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.DAGGER_SICKLE.id().toString())
            .withAdditionalSpell(ArsenalSpells.sundering_melee.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Sickles

    public static final Weapon.Entry unique_sickle_1 = add(Weapons.sickleWithSkill(ArsenalMod.NAMESPACE, "unique_sickle_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP))
            .translatedName("Toxic Sickle")
            .withAdditionalSpell(ArsenalSpells.poison_cloud_melee.id().toString())
            .lootTheme(Loot.Theme.GENERIC.toString()));
    public static final Weapon.Entry unique_sickle_2 = add(Weapons.sickleWithSkill(ArsenalMod.NAMESPACE, "unique_sickle_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.MAGMA_BLOCK))
            .translatedName("Infernal Harvester")
            .withAdditionalSpell(ArsenalSpells.exploding_melee.id().toString())
            .lootTheme(Loot.Theme.FIERY.toString()));
    public static final Weapon.Entry unique_sickle_sw = add(Weapons.sickleWithSkill(ArsenalMod.NAMESPACE, "unique_sickle_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Thalassian Sickle")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.SICKLE_AXE.id().toString())
            .withAdditionalSpell(ArsenalSpells.swirling_melee.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Longsword

    public static final Weapon.Entry unique_longsword_sw = add(Weapons.swordWithSkill(ArsenalMod.NAMESPACE, "unique_longsword_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_INGOT))
            .translatedName("Dragonscale-Encrusted Longblade")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.ONE_HANDED_SLASHER.id().toString())
            .withAdditionalSpell(ArsenalSpells.sundering_melee.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Double Axes

    public static final Weapon.Entry unique_double_axe_1 = add(Weapons.doubleAxeWithSkill(ArsenalMod.NAMESPACE, "unique_double_axe_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP))
            .translatedName("Dual-blade Butcher")
            .withAdditionalSpell(ArsenalSpells.leeching_melee.id().toString())
            .lootTheme(Loot.Theme.EVIL.toString()));
    public static final Weapon.Entry unique_double_axe_2 = add(Weapons.doubleAxeWithSkill(ArsenalMod.NAMESPACE, "unique_double_axe_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.IRON_BLOCK))
            .translatedName("Arcanite Reaper")
            .withAdditionalSpell(ArsenalSpells.wither_melee.id().toString())
            .lootTheme(Loot.Theme.EVIL.toString()));
    public static final Weapon.Entry unique_double_axe_sw = add(Weapons.doubleAxeWithSkill(ArsenalMod.NAMESPACE, "unique_double_axe_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_INGOT))
            .translatedName("Sunreaver War Axe")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.DOUBLE_AXE_HAMMER.id().toString())
            .withAdditionalSpell(ArsenalSpells.rampaging_melee.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Glaives

    public static final Weapon.Entry unique_glaive_1 = add(Weapons.glaiveWithSkill(ArsenalMod.NAMESPACE, "unique_glaive_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP))
            .translatedName("Hellreaver")
            .withAdditionalSpell(ArsenalSpells.flame_cloud_melee.id().toString())
            .lootTheme(Loot.Theme.FIERY.toString()));
    public static final Weapon.Entry unique_glaive_2 = add(Weapons.glaiveWithSkill(ArsenalMod.NAMESPACE, "unique_glaive_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.AMETHYST_BLOCK))
            .translatedName("Crystalforge Glaive")
            .withAdditionalSpell(ArsenalSpells.shockwave_melee.id().toString())
            .lootTheme(Loot.Theme.CRYSTAL.toString()));
    public static final Weapon.Entry unique_glaive_sw = add(Weapons.glaiveWithSkill(ArsenalMod.NAMESPACE, "unique_glaive_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Shivering Felspine")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.GLAIVE_DOUBLE_AXE.id().toString())
            .withAdditionalSpell(ArsenalSpells.swirling_melee.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Hammers

    public static final Weapon.Entry unique_hammer_1 = add(Weapons.hammerWithSkill(ArsenalMod.NAMESPACE, "unique_hammer_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.AMETHYST_BLOCK))
            .translatedName("Hammer of Destiny")
            .withAdditionalSpell(ArsenalSpells.shockwave_melee.id().toString())
            .lootTheme(Loot.Theme.CRYSTAL.toString()));
    public static final Weapon.Entry unique_hammer_2 = add(Weapons.hammerWithSkill(ArsenalMod.NAMESPACE, "unique_hammer_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP))
            .translatedName("Blackhand")
            .withAdditionalSpell(ArsenalSpells.exploding_melee.id().toString())
            .lootTheme(Loot.Theme.FIERY.toString()));
    public static final Weapon.Entry unique_hammer_sw = add(Weapons.hammerWithSkill(ArsenalMod.NAMESPACE, "unique_hammer_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Hammer of Sanctification")
            .withAdditionalSpell(ArsenalSpells.radiance_melee.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Maces

    public static final Weapon.Entry unique_mace_1 = add(Weapons.maceWithSkill(ArsenalMod.NAMESPACE, "unique_mace_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP))
            .translatedName("Bonecracker")
            .withAdditionalSpell(ArsenalSpells.sundering_melee.id().toString())
            .lootTheme(Loot.Theme.EVIL.toString()));
    public static final Weapon.Entry unique_mace_2 = add(Weapons.maceWithSkill(ArsenalMod.NAMESPACE, "unique_mace_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.IRON_BLOCK))
            .translatedName("Stormherald")
            .withAdditionalSpell(ArsenalSpells.stunning_melee.id().toString())
            .lootTheme(Loot.Theme.GENERIC.toString()));
    public static final Weapon.Entry unique_mace_sw = add(Weapons.maceWithSkill(ArsenalMod.NAMESPACE, "unique_mace_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Archon's Scepter")
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices(ArsenalSpellGroups.MACE_SWORD.id().toString())
            .withAdditionalSpell(ArsenalSpells.guarding_strike_melee.id().toString())
            .lootTheme(Loot.Theme.ELVEN.toString()));

    static {
        entries.forEach(entry -> entry.rarity = Rarity.RARE);
    }

    public static void register(Map<String, WeaponConfig> configs) {
        Weapon.register(configs, entries, Group.KEY);
    }
}
