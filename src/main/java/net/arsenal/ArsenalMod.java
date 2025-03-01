package net.arsenal;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.arsenal.item.Group;
import net.arsenal.spell.ArsenalEffects;
import net.arsenal.spell.ArsenalSounds;
import net.spell_engine.api.config.ConfigFile;
import net.tinyconfig.ConfigManager;

public class ArsenalMod implements ModInitializer {
    public static final String NAMESPACE = "arsenal";
    public static final String DIRECTORY = "relics";
    public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
            ("items", new ConfigFile.Equipment())
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

    @Override
    public void onInitialize() {
        ArsenalSounds.register();
        itemConfig.refresh();
        effectConfig.refresh();
        Group.GROUP = FabricItemGroup.builder()
                .icon(Group.ICON)
                .displayName(Text.translatable(Group.translationKey))
                .build();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.GROUP);
//        RelicItems.register(itemConfig.value.entries);
        itemConfig.save();
        ArsenalEffects.register(effectConfig.value);
        effectConfig.save();
    }
}
