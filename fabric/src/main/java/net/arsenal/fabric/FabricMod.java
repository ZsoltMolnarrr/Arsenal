package net.arsenal.fabric;

import net.arsenal.ArsenalMod;
import net.fabricmc.api.ModInitializer;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ArsenalMod.init();
        ArsenalMod.registerSounds();
        // Registers the item group too (see ArsenalMod#registerItems).
        ArsenalMod.registerItems();
        ArsenalMod.registerEffects();
    }
}
