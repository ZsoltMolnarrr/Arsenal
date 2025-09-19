package net.arsenal.fabric;

import net.arsenal.ArsenalMod;
import net.arsenal.item.Group;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ArsenalMod.init();
        ArsenalMod.registerSounds();

        // Create and register item group (Fabric-specific)
        Group.GROUP = FabricItemGroup.builder()
                .icon(Group.ICON)
                .displayName(Text.translatable(Group.translationKey))
                .build();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.GROUP);

        ArsenalMod.registerItems();
        ArsenalMod.registerEffects();
    }
}
