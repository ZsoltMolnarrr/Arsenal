package net.arsenal.forge;

import net.arsenal.ArsenalMod;
import net.arsenal.forge.client.ForgeClientMod;
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;

@Mod(ArsenalMod.NAMESPACE)
public final class ForgeMod {
    @SuppressWarnings("removal")
    public ForgeMod() {
        // Run our common setup (configs only — registers nothing).
        ArsenalMod.init();
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, ForgeMod::register);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClientMod.register(modBus);
        }
    }

    /// Forge 47 unfreezes exactly one registry per `RegisterEvent` window, so registration is split by
    /// registry. The creative-tab *contents* are dispatched by SpellEngine's `PlatformEvents.onItemGroupModify`
    /// (called from `Weapon.register` / `RangedWeapon.register` / `Shield.register`); the group itself is a
    /// vanilla-only registry that stays unfrozen for the whole phase, so registering it here is fine.
    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, reg -> ArsenalMod.registerSounds());
        // `registerItems` also creates + registers the `arsenal:generic` item group; that is a
        // vanilla-only registry which stays unfrozen for the whole RegisterEvent phase.
        event.register(RegistryKeys.ITEM, reg -> ArsenalMod.registerItems());
        event.register(RegistryKeys.STATUS_EFFECT, reg -> ArsenalMod.registerEffects());
    }
}
