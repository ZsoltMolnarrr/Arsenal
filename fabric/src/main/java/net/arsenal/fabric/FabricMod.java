package net.arsenal.fabric;

import net.arsenal.ArsenalMod;
import net.arsenal.item.Group;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ArsenalMod.init();
        ArsenalMod.registerSounds();

        // Create and register item group (Fabric-specific)
        Group.GROUP = FabricCreativeModeTab.builder()
                .icon(Group.ICON)
                .title(Component.translatable(Group.translationKey))
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Group.KEY, Group.GROUP);

        ArsenalMod.registerItems();
        ArsenalMod.registerEffects();
    }
}
