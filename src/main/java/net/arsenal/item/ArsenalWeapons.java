package net.arsenal.item;

import net.arsenal.ArsenalMod;
import net.arsenal.spell.ArsenalSpells;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.weapon.SpellSwordItem;
import net.spell_engine.api.item.weapon.SpellWeaponItem;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

public class ArsenalWeapons {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Weapon.Factory factory, WeaponConfig defaults, Equipment.WeaponType weaponType) {
        var entry = new Weapon.Entry(ArsenalMod.NAMESPACE, name, material, factory, defaults, weaponType);
        entry.castSpell();
        entry.loot(Equipment.LootProperties.of(5));
        entries.add(entry);
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString, boolean requirement, Item fallback) {
        var id = Identifier.of(idString);
        if (requirement) {
            return () -> {
                return Ingredient.ofItems(fallback);
            };
        } else {
            return () -> {
                var item = Registries.ITEM.get(id);
                var ingredient = item != null ? item : fallback;
                return Ingredient.ofItems(ingredient);
            };
        }
    }

    private static Weapon.Entry claymore(String name, Weapon.CustomMaterial material, float damage) {
        var entry = entry(name, material, SpellSwordItem::new, new WeaponConfig(damage, -3F), Equipment.WeaponType.CLAYMORE);
        entry.weaponAttributesPreset = "claymore";
        return entry;
    }
    private static Weapon.Entry hammer(String name, Weapon.CustomMaterial material, float damage) {
        var entry = entry(name, material, SpellWeaponItem::new, new WeaponConfig(damage, -3.2F), Equipment.WeaponType.HAMMER);
        entry.weaponAttributesPreset = "hammer";
        return entry;
    }
    private static final float staffAttackDamage = 4;
    private static final float staffAttackSpeed = -3F;
    private static Weapon.Entry damage_staff(String name, Weapon.CustomMaterial material) {
        var entry = entry(name, material, StaffItem::new, new WeaponConfig(staffAttackDamage, staffAttackSpeed), Equipment.WeaponType.DAMAGE_STAFF);
        entry.weaponAttributesPreset = "staff";
        return entry;
    }
    private static Weapon.Entry healing_staff(String name, Weapon.CustomMaterial material) {
        var entry = entry(name, material, StaffItem::new, new WeaponConfig(staffAttackDamage, staffAttackSpeed), Equipment.WeaponType.HEALING_STAFF);
        entry.weaponAttributesPreset = "staff";
        return entry;
    }
    private static Weapon.Entry spear(String name, Weapon.CustomMaterial material, float damage) {
        var entry = entry(name, material, SpellWeaponItem::new, new WeaponConfig(damage, -2.6F), Equipment.WeaponType.SPEAR);
        entry.weaponAttributesPreset = "spear";
        return entry;
    }
    private static Weapon.Entry mace(String name, Weapon.CustomMaterial material, float damage) {
        var entry = entry(name, material, SpellWeaponItem::new, new WeaponConfig(damage, -2.8F), Equipment.WeaponType.MACE);
        entry.weaponAttributesPreset = "mace";
        return entry;
    }
    private static Weapon.Entry glaive(String name, Weapon.CustomMaterial material, float damage) {
        var entry = entry(name, material, SpellSwordItem::new, new WeaponConfig(damage, -2.6F), Equipment.WeaponType.GLAIVE);
        entry.weaponAttributesPreset = "glaive";
        return entry;
    }
    private static Weapon.Entry axe(String name, Weapon.CustomMaterial material, float damage) {
        var entry = entry(name, material, SpellSwordItem::new, new WeaponConfig(damage, -2.8F), Equipment.WeaponType.DOUBLE_AXE);
        entry.weaponAttributesPreset = "double_axe";
        return entry;
    }
    private static Weapon.Entry dagger(String name, Weapon.CustomMaterial material, float damage) {
        var entry = entry(name, material, SpellSwordItem::new, new WeaponConfig(damage, -1.6F), Equipment.WeaponType.DAGGER);
        entry.weaponAttributesPreset = "dagger";
        return entry;
    }
    private static Weapon.Entry sickle(String name, Weapon.CustomMaterial material, float damage) {
        var entry = entry(name, material, SpellSwordItem::new, new WeaponConfig(damage, -2F), Equipment.WeaponType.SICKLE);
        entry.weaponAttributesPreset = "sickle";
        return entry;
    }


    public static final float TIER_5_SPELL_POWER = 7;

    public static final Weapon.Entry unique_claymore_1 = claymore("unique_claymore_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.MAGMA_BLOCK)), 13F)
            .translatedName("Cataclysm's Edge")
            .spell(ArsenalSpells.exploding_melee.id());
    public static final Weapon.Entry unique_claymore_2 = claymore("unique_claymore_2",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)), 13F)
            .translatedName("Champion's Greatsword")
            .spell(ArsenalSpells.radiance_melee.id());
    public static final Weapon.Entry unique_claymore_sw = claymore("unique_claymore_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 13F)
            .translatedName("Apolyon, the Soul-Render")
            .spell(ArsenalSpells.rampaging_melee.id());

    public static final Weapon.Entry unique_staff_damage_1 = damage_staff("unique_staff_damage_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_CLUSTER)))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, TIER_5_SPELL_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, TIER_5_SPELL_POWER))
            .translatedName("Nexus Key")
            .spell(ArsenalSpells.cooldown_shot_spell.id());
    public static final Weapon.Entry unique_staff_damage_2 = damage_staff("unique_staff_damage_2",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, TIER_5_SPELL_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, TIER_5_SPELL_POWER))
            .translatedName("Antonidas's Staff of Rapt Concentration")
            .spell(ArsenalSpells.chain_reaction_spell.id());
    public static final Weapon.Entry unique_staff_damage_3 = damage_staff("unique_staff_damage_3",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, TIER_5_SPELL_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, TIER_5_SPELL_POWER))
            .translatedName("Draconic Battle Staff")
            .spell(ArsenalSpells.flame_cloud_spell.id());
    public static final Weapon.Entry unique_staff_damage_4 = damage_staff("unique_staff_damage_4",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP)))
            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, TIER_5_SPELL_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, TIER_5_SPELL_POWER))
            .translatedName("Gargoyle's Bite")
            .spell(ArsenalSpells.leeching_spell.id());
    public static final Weapon.Entry unique_staff_damage_5 = damage_staff("unique_staff_damage_5",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_BLOCK)))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, TIER_5_SPELL_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, TIER_5_SPELL_POWER))
            .translatedName("Mage Lord Cane")
            .spell(ArsenalSpells.shockwave_area_spell.id());
    public static final Weapon.Entry unique_staff_damage_6 = damage_staff("unique_staff_damage_6",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.MAGMA_BLOCK)))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, TIER_5_SPELL_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, TIER_5_SPELL_POWER))
            .translatedName("Endless Winter")
            .spell(ArsenalSpells.frost_cloud_spell.id());

    public static final Weapon.Entry unique_staff_damage_sw = damage_staff("unique_staff_damage_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, TIER_5_SPELL_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, TIER_5_SPELL_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, TIER_5_SPELL_POWER))
            .translatedName("Grand Magister's Staff of Torrents")
            .spell(ArsenalSpells.rampaging_spell.id());

    public static final Weapon.Entry unique_staff_heal_1 = healing_staff("unique_staff_heal_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_CLUSTER)))
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, TIER_5_SPELL_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, TIER_5_SPELL_POWER))
            .translatedName("Crystalline Life-Staff")
            .spell(ArsenalSpells.radiance_spell.id());
    public static final Weapon.Entry unique_staff_heal_2 = healing_staff("unique_staff_heal_2",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)))
            .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, TIER_5_SPELL_POWER))
            .translatedName("Staff of Immaculate Recovery")
            .spell(ArsenalSpells.guardian_heal.id());
    public static final Weapon.Entry unique_staff_heal_sw = healing_staff("unique_staff_heal_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, TIER_5_SPELL_POWER))
            .translatedName("Golden Staff of the Sin'dorei")
            .spell(ArsenalSpells.cooldown_heal.id());

    public static final Weapon.Entry unique_spear_1 = spear("unique_spear_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP)), 8F)
            .translatedName("Sonic Spear")
            .spell(ArsenalSpells.slowing_melee.id());
    public static final Weapon.Entry unique_spear_2 = spear("unique_spear_2",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), 8F)
            .translatedName("Spear of the Damned")
            .spell(ArsenalSpells.stunning_melee.id());
    public static final Weapon.Entry unique_spear_sw = spear("unique_spear_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 8F)
            .translatedName("Mounting Vengeance")
            .spell(ArsenalSpells.leeching_melee.id());

    public static final Weapon.Entry unique_dagger_1 = dagger("unique_dagger_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.PRISMARINE)), 5.5F)
            .translatedName("Frost Fang")
            .spell(ArsenalSpells.slowing_melee.id());
    public static final Weapon.Entry unique_dagger_2 = dagger("unique_dagger_2",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP)), 5.5F)
            .translatedName("Demonic Shiv")
            .spell(ArsenalSpells.leeching_melee.id());
    public static final Weapon.Entry unique_dagger_sw = dagger("unique_dagger_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), 5.5F)
            .translatedName("Crux of the Apocalypse")
            .spell(ArsenalSpells.sundering_melee.id());;

    public static final Weapon.Entry unique_sickle_1 = sickle("unique_sickle_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP)), 6.8F)
            .translatedName("Toxic Sickle")
            .spell(ArsenalSpells.poison_cloud_melee.id());
    public static final Weapon.Entry unique_sickle_2 = sickle("unique_sickle_2",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.MAGMA_BLOCK)), 6.8F)
            .translatedName("Infernal Harvester")
            .spell(ArsenalSpells.exploding_melee.id());;
    public static final Weapon.Entry unique_sickle_sw = sickle("unique_sickle_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 6.8F)
            .translatedName("Thalassian Sickle")
            .spell(ArsenalSpells.swirling_melee.id());

//    public static final Weapon.Entry unique_longsword_sw = sickle("unique_longsword_sw",
//            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 6.8F)
//            .translatedName("Dragonscale-Encrusted Longblade");


    public static final Weapon.Entry unique_double_axe_1 = axe("unique_double_axe_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP)), 11F)
            .translatedName("Dual-blade Butcher")
            .spell(ArsenalSpells.leeching_melee.id());
    public static final Weapon.Entry unique_double_axe_2 = axe("unique_double_axe_2",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)), 11F)
            .translatedName("Arcanite Reaper")
            .spell(ArsenalSpells.wither_melee.id());
    public static final Weapon.Entry unique_double_axe_sw = axe("unique_double_axe_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 11F)
            .translatedName("Sunreaver War Axe")
            .spell(ArsenalSpells.rampaging_melee.id());

    public static final Weapon.Entry unique_glaive_1 = glaive("unique_glaive_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP)), 9.3F)
            .translatedName("Hellreaver")
            .spell(ArsenalSpells.flame_cloud_melee.id());
    public static final Weapon.Entry unique_glaive_2 = glaive("unique_glaive_2",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_BLOCK)), 9.3F)
            .translatedName("Crystalforge Glaive")
            .spell(ArsenalSpells.shockwave_melee.id());
    public static final Weapon.Entry unique_glaive_sw = glaive("unique_glaive_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), 9.3F)
            .translatedName("Shivering Felspine")
            .spell(ArsenalSpells.swirling_melee.id());

    public static final Weapon.Entry unique_hammer_1 = hammer("unique_hammer_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_BLOCK)), 16F)
            .translatedName("Hammer of Destiny")
            .spell(ArsenalSpells.shockwave_melee.id());
    public static final Weapon.Entry unique_hammer_sw = hammer("unique_hammer_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), 16F)
            .translatedName("Hammer of Sanctification")
            .spell(ArsenalSpells.radiance_melee.id());

    public static final Weapon.Entry unique_mace_1 = mace("unique_mace_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP)), 11F)
            .translatedName("Bonecracker")
            .spell(ArsenalSpells.sundering_melee.id());
    public static final Weapon.Entry unique_mace_sw = mace("unique_mace_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), 11F)
            .translatedName("Archon's Scepter")
            .spell(ArsenalSpells.guarding_strike_melee.id());

    static {
        entries.forEach(entry -> entry.rarity = Rarity.RARE);
    }

    public static void register(Map<String, WeaponConfig> configs) {
        Weapon.register(configs, entries, Group.KEY);
    }
}
