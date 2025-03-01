package net.arsenal.item;

import net.arsenal.ArsenalMod;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.api.item.weapon.SpellSwordItem;
import net.spell_engine.api.item.weapon.SpellWeaponItem;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.item.weapon.Weapon;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

public class ArsenalWeapons {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Weapon.Factory factory, WeaponConfig defaults) {
        var entry = new Weapon.Entry(ArsenalMod.NAMESPACE, name, material, factory, defaults, null);
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
        var entry = entry(name, material, SpellSwordItem::new, new WeaponConfig(damage, -3F));
        entry.weaponAttributesPreset = "claymore";
        return entry;
    }
    private static Weapon.Entry hammer(String name, Weapon.CustomMaterial material, float damage) {
        var entry = entry(name, material, SpellWeaponItem::new, new WeaponConfig(damage, -3.2F));
        entry.weaponAttributesPreset = "hammer";
        return entry;
    }
    private static final float staffAttackDamage = 4;
    private static final float staffAttackSpeed = -3F;
    private static Weapon.Entry staff(String name, Weapon.CustomMaterial material) {
        var entry = entry(name, material, StaffItem::new, new WeaponConfig(staffAttackDamage, staffAttackSpeed));
        entry.weaponAttributesPreset = "staff";
        return entry;
    }


    public static final Weapon.Entry unique_claymore_1 = claymore("unique_claymore_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.MAGMA_BLOCK)), 13F)
            .translatedName("Cataclysm's Edge");

    public static final Weapon.Entry unique_staff_1 = staff("unique_staff_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_CLUSTER)))
            .translatedName("Exodar Life-Staff");

    public static final Weapon.Entry unique_hammer_1 = hammer("unique_hammer_1",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_BLOCK)), 16F)
            .translatedName("Hammer of the Naaru");
    public static final Weapon.Entry unique_hammer_sw = hammer("unique_hammer_sw",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), 16F)
            .translatedName("Hammer of Sanctification");

    static {
        entries.forEach(entry -> entry.rarity = Rarity.RARE);
    }

    public static void register(Map<String, WeaponConfig> configs) {
        Weapon.register(configs, entries, Group.KEY);
    }
}
