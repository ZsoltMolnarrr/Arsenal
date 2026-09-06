package net.arsenal;

import net.arsenal.config.RangedConfigFile;
import net.arsenal.item.ArsenalBows;
import net.arsenal.item.ArsenalShields;
import net.arsenal.item.ArsenalWeapons;
import net.arsenal.spell.ArsenalEffects;
import net.arsenal.spell.ArsenalSounds;
import net.arsenal.item.Group;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.tiny_config.ConfigManager;

public class ArsenalMod {
    public static final String NAMESPACE = "arsenal";
    public static final String DIRECTORY = NAMESPACE;
    public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
            ("equipment_v2", new ConfigFile.Equipment())
            .builder()
            .setDirectory(DIRECTORY)
            .sanitize(true)
            .build();
    public static ConfigManager<RangedConfigFile> rangedConfig = new ConfigManager<>
            ("ranged_weapons", new RangedConfigFile())
            .builder()
            .setDirectory(DIRECTORY)
            .sanitize(true)
            .build();
    public static ConfigManager<ConfigFile.Shields> shieldConfig = new ConfigManager<>
            ("shields", new ConfigFile.Shields())
            .builder()
            .setDirectory(DIRECTORY)
            .sanitize(true)
            .build();
    public static ConfigManager<ConfigFile.Effects> effectConfig = new ConfigManager<>
            ("effects", new ConfigFile.Effects())
            .builder()
            .setDirectory(DIRECTORY)
            .sanitize(true)
            .build();

    public static void init() {
        itemConfig.refresh();
        rangedConfig.refresh();
        shieldConfig.refresh();
        effectConfig.refresh();
    }

    public static void registerSounds() {
        ArsenalSounds.register();
    }

    public static void registerItems() {
        // The item group is created here (not per-loader) so both entrypoints share one code path.
        // On Forge this runs inside the `ITEM` RegisterEvent window; `minecraft:item_group` is a
        // vanilla-only registry that stays unfrozen for the whole phase, so that is fine.
        Group.GROUP = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
                .icon(Group.ICON)
                .displayName(Text.translatable(Group.translationKey))
                .build();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.GROUP);
        ArsenalWeapons.register(itemConfig.value.weapons);
        itemConfig.save();
        ArsenalBows.register(rangedConfig.value.ranged_weapons);
        rangedConfig.save();
        ArsenalShields.register(shieldConfig.value.shields);
        shieldConfig.save();
    }

    public static void registerEffects() {
        ArsenalEffects.register(effectConfig.value);
        effectConfig.save();
    }
}
