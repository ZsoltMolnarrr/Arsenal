package net.arsenal.item;

import net.arsenal.ArsenalMod;
import net.arsenal.spell.ArsenalSounds;
import net.arsenal.spell.ArsenalSpells;
import net.fabric_extras.shield_api.item.CustomShieldItem;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Rarity;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.ShieldConfig;
import net.spell_engine.api.spell.container.SpellContainers;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Shield;
import net.spell_engine.rpg_series.item.Shields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArsenalShields {
    public static final ArrayList<Shield.Entry> entries = new ArrayList<>();

    private static Shield.Entry add(Shield.Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static final List<AttributeModifier> UNIQUE_ATTRIBUTES =
            List.of(Shields.toughness(2), Shields.health(6));

    // MARK: Shields

    public static Shield.Entry unique_shield_1 = add(Shields.create(ArsenalMod.NAMESPACE, "unique_shield_1", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.NETHERITE_SCRAP), UNIQUE_ATTRIBUTES, ArsenalSounds.shield_equip.entry())
            .translatedName("Bulwark of Azzinoth")
            .spellContainer(SpellContainers.forRelic(ArsenalSpells.spiked_shield.id()))
            .lootTheme(Loot.Theme.EVIL.toString()));
    public static Shield.Entry unique_shield_2 = add(Shields.create(ArsenalMod.NAMESPACE, "unique_shield_2", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.IRON_BLOCK), UNIQUE_ATTRIBUTES, ArsenalSounds.shield_equip.entry())
            .translatedName("Bastion of Light")
            .spellContainer(SpellContainers.forRelic(ArsenalSpells.guarding_shield.id()))
            .lootTheme(Loot.Theme.GENERIC.toString()));
    public static Shield.Entry unique_shield_sw = add(Shields.create(ArsenalMod.NAMESPACE, "unique_shield_sw", Equipment.Tier.TIER_5, () -> Ingredient.ofItems(Items.GOLD_BLOCK), UNIQUE_ATTRIBUTES, ArsenalSounds.shield_equip.entry())
            .translatedName("Sword Breaker's Bulwark")
            .spellContainer(SpellContainers.forRelic(ArsenalSpells.unyielding_shield.id()))
            .lootTheme(Loot.Theme.ELVEN.toString()));

    static {
        for (var entry: entries) {
            entry.rarity = Rarity.RARE;
        }
    }

    public static void register(Map<String, ShieldConfig> configs) {
        Shield.register(configs, entries, Group.KEY, CustomShieldItem::new);
    }
}
