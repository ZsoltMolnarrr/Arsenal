package net.arsenal.fabric;

import net.arsenal.ArsenalMod;
import net.fabricmc.api.ModInitializer;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ArsenalMod.init();
    }
}
