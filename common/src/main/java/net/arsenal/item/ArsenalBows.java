package net.arsenal.item;

import net.arsenal.ArsenalMod;
import net.arsenal.spell.ArsenalSpells;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Rarity;
import net.spell_engine.api.spell.container.SpellContainers;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.RangedWeapon;
import net.spell_engine.rpg_series.item.RangedWeapons;

import java.util.ArrayList;
import java.util.Map;

public class ArsenalBows {
    public static final ArrayList<RangedWeapon.Entry> entries = new ArrayList<>();

    private static RangedWeapon.Entry addBow(RangedWeapon.Entry entry) {
        entry.weaponAttributesPreset = "bow_two_handed_heavy";
        entries.add(entry);
        return entry;
    }

    private static RangedWeapon.Entry addCrossbow(RangedWeapon.Entry entry) {
        entry.weaponAttributesPreset = "crossbow_two_handed_heavy";
        entries.add(entry);
        return entry;
    }

    // MARK: Longbows

    public static RangedWeapon.Entry unique_longbow_1 = addBow(RangedWeapons.longBow(ArsenalMod.NAMESPACE, "unique_longbow_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Sunfury Hawk-Bow")
            .spellContainer(SpellContainers.forRangedWeapon().withSpellId(ArsenalSpells.radiance_ranged.id()))
            .lootTheme(Loot.Theme.DIVINE.toString()));
    public static RangedWeapon.Entry unique_longbow_2 = addBow(RangedWeapons.longBow(ArsenalMod.NAMESPACE, "unique_longbow_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP))
            .translatedName("Black Bow of the Betrayer")
            .spellContainer(SpellContainers.forRangedWeapon().withSpellId(ArsenalSpells.wither_ranged.id()))
            .lootTheme(Loot.Theme.EVIL.toString()));
    public static RangedWeapon.Entry unique_longbow_sw = addBow(RangedWeapons.longBow(ArsenalMod.NAMESPACE, "unique_longbow_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Golden Bow of Quel'Thalas")
            .spellContainer(SpellContainers.forRangedWeapon().withSpellId(ArsenalSpells.rampaging_ranged.id()))
            .lootTheme(Loot.Theme.ELVEN.toString()));

    // MARK: Heavy Crossbows

    public static RangedWeapon.Entry unique_heavy_crossbow_1 = addCrossbow(RangedWeapons.heavyCrossbow(ArsenalMod.NAMESPACE, "unique_heavy_crossbow_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP))
            .translatedName("Heavy Crossbow of the Phoenix")
            .spellContainer(SpellContainers.forRangedWeapon().withSpellId(ArsenalSpells.flame_cloud_ranged.id()))
            .lootTheme(Loot.Theme.FIERY.toString()));
    public static RangedWeapon.Entry unique_heavy_crossbow_2 = addCrossbow(RangedWeapons.heavyCrossbow(ArsenalMod.NAMESPACE, "unique_heavy_crossbow_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.BONE_BLOCK))
            .translatedName("Necropolis Ballista")
            .spellContainer(SpellContainers.forRangedWeapon().withSpellId(ArsenalSpells.poison_cloud_ranged.id()))
            .lootTheme(Loot.Theme.EVIL.toString()));
    public static RangedWeapon.Entry unique_heavy_crossbow_sw = addCrossbow(RangedWeapons.heavyCrossbow(ArsenalMod.NAMESPACE, "unique_heavy_crossbow_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK))
            .translatedName("Crossbow of Relentless Strikes")
            .spellContainer(SpellContainers.forRangedWeapon().withSpellId(ArsenalSpells.bonus_shot_ranged.id()))
            .lootTheme(Loot.Theme.ELVEN.toString()));

    static {
        for (var entry: entries) {
            entry.rarity = Rarity.RARE;
        }
    }

    public static void register(Map<String, RangedConfig> rangedConfig) {
        RangedWeapon.register(rangedConfig, entries, Group.KEY);
    }
}
