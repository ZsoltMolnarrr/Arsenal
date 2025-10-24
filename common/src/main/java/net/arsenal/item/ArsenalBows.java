package net.arsenal.item;

import net.arsenal.ArsenalMod;
import net.arsenal.spell.ArsenalSpells;
import net.fabric_extras.ranged_weapon.api.CustomBow;
import net.fabric_extras.ranged_weapon.api.CustomCrossbow;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.Tiers;
import net.spell_engine.api.spell.SpellDataComponents;
import net.spell_engine.api.spell.container.SpellContainerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArsenalBows {

    public interface RangedFactory {
        Item create(Item.Settings settings, RangedConfig config, Supplier<Ingredient> repairIngredientSupplier);
    }

    public static final class RangedEntry {
        private final Identifier id;
        private final RangedFactory factory;
        private final RangedConfig defaults;
        private final Supplier<Ingredient> repairIngredientSupplier;
        private final int durability;
        public String weaponAttributesPreset;
        private String translatedName = "";
        public Rarity rarity = Rarity.COMMON;
        public Item item;
        public List<Identifier> spells = null;

        public Equipment.LootProperties lootProperties = Equipment.LootProperties.EMPTY;
        public Equipment.WeaponType weaponType = Equipment.WeaponType.LONG_BOW;

        public RangedEntry(Identifier id, RangedFactory factory, RangedConfig defaults, Supplier<Ingredient> repairIngredientSupplier, int durability) {
            this.id = id;
            this.factory = factory;
            this.defaults = defaults;
            this.repairIngredientSupplier = repairIngredientSupplier;
            this.durability = durability;
        }

        public Identifier id() {
            return id;
        }

        public RangedFactory factory() {
            return factory;
        }

        public RangedConfig defaults() {
            return defaults;
        }

        public Supplier<Ingredient> repairIngredientSupplier() {
            return repairIngredientSupplier;
        }

        public int durability() {
            return durability;
        }

        public Item create(Item.Settings settings, RangedConfig config) {
            this.item = factory.create(
                    settings.maxDamage(durability),
                    config,
                    repairIngredientSupplier
            );
            return this.item;
        }

        public Item item() {
            return item;
        }

        public RangedEntry translatedName(String translatedName) {
            this.translatedName = translatedName;
            return this;
        }

        public String translatedName() {
            return translatedName;
        }

        public String translationKey() {
            return Util.createTranslationKey("item", id());
        }

        public RangedEntry castSpell() {
            spells = List.of();
            return this;
        }

        public RangedEntry spell(Identifier spellId) {
            spells = List.of(spellId);
            return this;
        }

        public RangedEntry loot(int tier, String theme) {
            this.lootProperties = Equipment.LootProperties.of(tier, theme);
            return this;
        }
    }
    public static final ArrayList<RangedEntry> entries = new ArrayList<>();

    private static final int durabilityTier0 = 384;
    private static final int durabilityTier1 = 465; // Matches Vanilla Crossbow durability
    private static final int durabilityTier2 = ToolMaterials.DIAMOND.getDurability();
    private static final int durabilityTier3 = ToolMaterials.NETHERITE.getDurability();
    private static final int durabilityTier4 = durabilityTier3 * 2;

    private static final float pullTime_shortBow = 0.8F - 1F;
    private static final float pullTime_longBow = 1.5F - 1F;
    private static final float pullTime_rapidCrossbow = 0;
    private static final float pullTime_heavyCrossbow = 1.75F - 1F;
    private static final float velocity_shortBow = 0F;
    private static final float velocity_longBow = 0.75F;
    private static final float velocity_rapidCrossbow = 0F;
    private static final float velocity_heavyCrossbow = 0.5F;

    private static RangedEntry bow(String name, int durability, Supplier<Ingredient> repairIngredientSupplier, RangedConfig defaults) {
        var entry = new RangedEntry(Identifier.of(ArsenalMod.NAMESPACE, name), CustomBow::new, defaults, repairIngredientSupplier, durability);
        entry.weaponAttributesPreset = "bow_two_handed_heavy";
        entry.weaponType = Equipment.WeaponType.LONG_BOW;
        entry.lootProperties = Equipment.LootProperties.of(Loot.TIER);
        entries.add(entry);
        return entry;
    }

    private static RangedEntry crossbow(String name, int durability, Supplier<Ingredient> repairIngredientSupplier, RangedConfig defaults) {
        var entry = new RangedEntry(Identifier.of(ArsenalMod.NAMESPACE, name), CustomCrossbow::new, defaults, repairIngredientSupplier, durability);
        entry.weaponAttributesPreset = "crossbow_two_handed_heavy";
        entry.weaponType = Equipment.WeaponType.HEAVY_CROSSBOW;
        entry.lootProperties = Equipment.LootProperties.of(Loot.TIER);
        entries.add(entry);
        return entry;
    }

    public static RangedEntry unique_longbow_1 = bow("unique_longbow_1", durabilityTier4,
            () -> Ingredient.ofItems(Items.GOLD_BLOCK),
            new RangedConfig(13.5F, pullTime_longBow, velocity_longBow))
            .translatedName("Sunfury Hawk-Bow")
            .spell(ArsenalSpells.radiance_ranged.id())
            .loot(Loot.TIER, Loot.Theme.DIVINE.toString());
    public static RangedEntry unique_longbow_2 = bow("unique_longbow_2", durabilityTier4,
            () -> Ingredient.ofItems(Items.NETHERITE_SCRAP),
            new RangedConfig(13.5F, pullTime_longBow, velocity_longBow))
            .translatedName("Black Bow of the Betrayer")
            .spell(ArsenalSpells.wither_ranged.id())
            .loot(Loot.TIER, Loot.Theme.EVIL.toString());
    public static RangedEntry unique_longbow_sw = bow("unique_longbow_sw", durabilityTier4,
            () -> Ingredient.ofItems(Items.GOLD_BLOCK),
            new RangedConfig(13.5F, pullTime_longBow, velocity_longBow))
            .translatedName("Golden Bow of Quel'Thalas")
            .spell(ArsenalSpells.rampaging_ranged.id())
            .loot(Loot.TIER, Loot.Theme.ELVEN.toString());

    public static RangedEntry unique_heavy_crossbow_1 = crossbow("unique_heavy_crossbow_1", durabilityTier4,
            () -> Ingredient.ofItems(Items.NETHERITE_SCRAP),
            new RangedConfig(17F, pullTime_heavyCrossbow, velocity_heavyCrossbow))
            .translatedName("Heavy Crossbow of the Phoenix")
            .spell(ArsenalSpells.flame_cloud_ranged.id())
            .loot(Loot.TIER, Loot.Theme.FIERY.toString());
    public static RangedEntry unique_heavy_crossbow_2 = crossbow("unique_heavy_crossbow_2", durabilityTier4,
            () -> Ingredient.ofItems(Items.BONE_BLOCK),
            new RangedConfig(17F, pullTime_heavyCrossbow, velocity_heavyCrossbow))
            .translatedName("Necropolis Ballista")
            .spell(ArsenalSpells.poison_cloud_ranged.id())
            .loot(Loot.TIER, Loot.Theme.EVIL.toString());
    public static RangedEntry unique_heavy_crossbow_sw = crossbow("unique_heavy_crossbow_sw", durabilityTier4,
            () -> Ingredient.ofItems(Items.GOLD_BLOCK),
            new RangedConfig(17F, pullTime_heavyCrossbow, velocity_heavyCrossbow))
            .translatedName("Crossbow of Relentless Strikes")
            .spell(ArsenalSpells.bonus_shot_ranged.id())
            .loot(Loot.TIER, Loot.Theme.ELVEN.toString());

    static {
        for (var entry: entries) {
            entry.rarity = Rarity.RARE;
        }
    }

    public static void register(Map<String, RangedConfig> rangedConfig) {
        var netheriteTier = Tiers.unsafe("netherite");

        for (var entry: entries) {
            var config = rangedConfig.get(entry.id.toString());
            if (config == null) {
                config = entry.defaults;
                rangedConfig.put(entry.id.toString(), config);
            }
            var settings = new Item.Settings();
            var tier = Tiers.unsafe(entry.id());
            if (tier >= netheriteTier) {
                settings.fireproof();
            }
            if (entry.rarity != Rarity.COMMON) {
                settings.rarity(entry.rarity);
            }
            if (entry.spells != null) {
                if (entry.spells.isEmpty()) {
                    settings.component(SpellDataComponents.SPELL_CONTAINER, SpellContainerHelper.createForRangedWeapon());
                } else {
                    settings.component(SpellDataComponents.SPELL_CONTAINER, SpellContainerHelper.createForRangedWeapon(entry.spells));
                }
            }
            var item = entry.create(settings, config);
            Registry.register(Registries.ITEM, entry.id, item);
        }
        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register((content) -> {
            for (var entry: entries) {
                content.add(entry.item);
            }
        });
    }
}
