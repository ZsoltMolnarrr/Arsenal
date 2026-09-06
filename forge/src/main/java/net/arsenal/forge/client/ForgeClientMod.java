package net.arsenal.forge.client;

import net.arsenal.client.ArsenalClientMod;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/// Client-side Forge wiring. Only ever touched behind `Dist.CLIENT` (see `ForgeMod`), so there is no
/// `@EventBusSubscriber` — the listener is registered explicitly from {@link #register(IEventBus)}.
public class ForgeClientMod {
    public static void register(IEventBus modBus) {
        modBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, ForgeClientMod::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        ArsenalClientMod.init();
    }
}
