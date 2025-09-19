package net.arsenal.fabric.client;

import net.arsenal.client.ArsenalClientMod;
import net.fabricmc.api.ClientModInitializer;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ArsenalClientMod.init();
    }
}
