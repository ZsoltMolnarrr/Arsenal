package net.arsenal.neoforge.client;

import net.arsenal.ArsenalMod;
import net.arsenal.client.ArsenalClientMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ArsenalMod.NAMESPACE, value = Dist.CLIENT)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ArsenalClientMod.init();
    }
}