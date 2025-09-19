package net.arsenal;

import net.arsenal.config.RangedConfigFile;
import net.arsenal.item.ArsenalBows;
import net.arsenal.item.ArsenalShields;
import net.arsenal.item.ArsenalWeapons;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.arsenal.item.Group;
import net.arsenal.spell.ArsenalEffects;
import net.arsenal.spell.ArsenalSounds;
import net.spell_engine.api.config.ConfigFile;
import net.tiny_config.ConfigManager;

public class ArsenalMod {
    public static final String NAMESPACE = "arsenal";
    public static final String DIRECTORY = NAMESPACE;
    public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
            ("equipment", new ConfigFile.Equipment())
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
        ArsenalSounds.register();

        ArsenalEffects.register(effectConfig.value);
        effectConfig.save();

        Group.GROUP = FabricItemGroup.builder()
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
}
