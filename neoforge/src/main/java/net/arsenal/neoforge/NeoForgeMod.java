package net.arsenal.neoforge;

import net.arsenal.ArsenalMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.arsenal.item.Group;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

@Mod(ArsenalMod.NAMESPACE)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        // Run our common setup.
        ArsenalMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
    }

    public static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, reg -> {
            ArsenalMod.registerSounds();
        });
        event.register(Registries.CREATIVE_MODE_TAB, reg -> {
            // Create and register item group (NeoForge-specific)
            Group.GROUP = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
                    .icon(Group.ICON)
                    .title(Component.translatable(Group.translationKey))
                    .build();
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Group.KEY, Group.GROUP);
        });
        event.register(Registries.ITEM, reg -> {
            ArsenalMod.registerItems();
        });
        event.register(Registries.MOB_EFFECT, reg -> {
            ArsenalMod.registerEffects();
        });
    }
}
