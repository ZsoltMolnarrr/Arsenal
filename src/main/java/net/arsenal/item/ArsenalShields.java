package net.arsenal.item;

import net.arsenal.ArsenalMod;
import net.arsenal.spell.ArsenalSounds;
import net.arsenal.spell.ArsenalSpells;
import net.fabric_extras.shield_api.item.CustomShieldItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.ShieldConfig;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.Tiers;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_engine.api.spell.SpellDataComponents;
import net.spell_engine.api.spell.container.SpellContainerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArsenalShields {
    public static final class Entry {
        private final Identifier id;
        private final Supplier<Ingredient> repair;
        private final List<AttributeModifier> attributes;
        private final int durability;
        private String translatedName = "";
        public Rarity rarity = Rarity.COMMON;
        public List<Identifier> spells = null;

        public Equipment.LootProperties lootProperties = Equipment.LootProperties.EMPTY;

        public Entry(Identifier id, Supplier<Ingredient> repair, List<AttributeModifier> attributes, int durability) {
            this.id = id;
            this.repair = repair;
            this.attributes = attributes;
            this.durability = durability;
        }

        public Identifier id() {
            return id;
        }

        public Supplier<Ingredient> repair() {
            return repair;
        }

        public List<AttributeModifier> attributes() {
            return attributes;
        }

        public int durability() {
            return durability;
        }

        public Entry translatedName(String translatedName) {
            this.translatedName = translatedName;
            return this;
        }

        public String translatedName() {
            return translatedName;
        }

        public String translationKey() {
            return Util.createTranslationKey("item", id());
        }

        public Entry spell(Identifier spellId) {
            spells = List.of(spellId);
            return this;
        }
    }

    public static final ArrayList<Entry> entries = new ArrayList<>();

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

    public static Entry shield(String name, Supplier<Ingredient> repair, List<AttributeModifier> attributes, int durability) {
        var entry = new Entry(Identifier.of(ArsenalMod.NAMESPACE, name), repair, attributes, durability);
        entry.lootProperties = Equipment.LootProperties.of(5);
        entries.add(entry);
        return entry;
    }

    private static final String GENERIC_ARMOR_TOUGHNESS = "minecraft:generic.armor_toughness";
    private static final String GENERIC_MAX_HEALTH = "generic.max_health";

    private static final int durability_t0 = 168;
    private static final int durability_t1 = 336; // Matches vanilla shield
    private static final int durability_t2 = 672;
    private static final int durability_t3 = 1344;
    private static final int durability_t4 = 2688;

    public static Entry unique_shield_1 = shield("unique_shield_1",
            () -> Ingredient.ofItems(Items.NETHERITE_INGOT), List.of(
                    new AttributeModifier(GENERIC_ARMOR_TOUGHNESS,  2,  EntityAttributeModifier.Operation.ADD_VALUE),
                    new AttributeModifier(GENERIC_MAX_HEALTH,  6.0f,  EntityAttributeModifier.Operation.ADD_VALUE)
            ),
            durability_t4)
            .translatedName("Bulwark of Azzinoth")
            .spell(ArsenalSpells.spiked_shield.id());
    public static Entry unique_shield_sw = shield("unique_shield_sw",
            () -> Ingredient.ofItems(Items.NETHERITE_INGOT), List.of(
                    new AttributeModifier(GENERIC_ARMOR_TOUGHNESS,  2,  EntityAttributeModifier.Operation.ADD_VALUE),
                    new AttributeModifier(GENERIC_MAX_HEALTH,  6.0f,  EntityAttributeModifier.Operation.ADD_VALUE)
            ),
            durability_t4)
            .translatedName("Sword Breaker's Bulwark")
            .spell(ArsenalSpells.unyielding_shield.id());

    static {
        for (var entry: entries) {
            entry.rarity = Rarity.RARE;
        }
    }

    public static void register(Map<String, ShieldConfig> configs) {
        var netheriteTier = Tiers.unsafe("netherite");
        ArrayList<Item> shields = new ArrayList<>();
        for (var entry: entries) {
            var config = configs.get(entry.id.toString());
            if (config == null) {
                config = new ShieldConfig();
                config.durability = entry.durability;
                config.attributes = entry.attributes;
                configs.put(entry.id.toString(), config);
            }
            ArrayList<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> shieldAttributes = new ArrayList<>();
            for (var modifier: Weapon.attributesFrom(config.attributes).modifiers()) {
                shieldAttributes.add(new Pair<>(modifier.attribute(), modifier.modifier()));
            }
            var settings = new Item.Settings().maxDamage(config.durability);
            var tier = Tiers.unsafe(entry.id());
            if (tier >= netheriteTier) {
                settings.fireproof();
            }
            if (entry.rarity != Rarity.COMMON) {
                settings.rarity(entry.rarity);
            }
            if (entry.spells != null) {
                settings.component(SpellDataComponents.SPELL_CONTAINER, SpellContainerHelper.createForShield(entry.spells));
            }
            var shield = new CustomShieldItem(ArsenalSounds.shield_equip.entry(), entry.repair, shieldAttributes, settings);
            Registry.register(Registries.ITEM, entry.id, shield);
            shields.add(shield);
        }

        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register((content) -> {
            for (var shield: shields) {
                content.add(shield);
            }
        });
    }
}
