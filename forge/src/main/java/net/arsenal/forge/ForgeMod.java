package net.arsenal.forge;

import net.arsenal.ArsenalMod;
import net.arsenal.forge.client.ForgeClientMod;
import net.arsenal.item.ArsenalBows;
import net.arsenal.item.ArsenalShields;
import net.arsenal.item.ArsenalWeapons;
import net.arsenal.item.Group;
import net.arsenal.spell.ArsenalEffects;
import net.arsenal.spell.ArsenalSounds;
import net.fabric_extras.shield_api.item.CustomShieldItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import net.spell_engine.api.effect.Effects;
import net.spell_engine.fx.SpellEngineSounds;
import net.spell_engine.rpg_series.item.RangedWeapon;
import net.spell_engine.rpg_series.item.Shield;
import net.spell_engine.rpg_series.item.Weapon;

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

    /// One listener for every registry; `RegisterEvent#register(key, consumer)` only runs the consumer when
    /// the event is for that key, so each block below executes inside exactly its own registry's window.
    ///
    /// The loops are **duplicated here on purpose** rather than delegated to `common`'s `registerX()`
    /// methods: a plain `Registry.register` is not usable on this loader, because Forge only clears the
    /// vanilla registry's own lock from 47.4.0 onwards — on 47.0–47.3 and NeoForge 1.20.1 it throws
    /// "Can not register to a locked registry" even inside the correct `RegisterEvent` window, and our
    /// `mods.toml` declares `loaderVersion = "[47,)"`. The helper this event hands out is the API every
    /// build of `[47,)` sanctions, so Forge iterates the same content `common` exposes and registers it
    /// itself. `common` keeps its vanilla-shaped `registerX()` for Fabric, which is untouched.
    ///
    /// Almost all of Arsenal's content is built by Spell Engine helpers, so the blocks below use their
    /// creation-only siblings (`…ItemsToRegister` / `soundsToRegister` / `effectsToRegister`, Spell Engine
    /// 1.10.5.034). Those install the creative-tab contents callback exactly as `register(...)` does; the
    /// *contents* are then dispatched by Spell Engine's `PlatformEvents.onItemGroupModify` bridge.
    ///
    /// `event.register` has no `else` and no throw, so content filed under a key that does not match the
    /// event vanishes silently — the grouping is deliberate, and the `arsenal:generic` item group gets its
    /// own block instead of riding along with the items as it used to: `creative_mode_tab` is registry
    /// event 65, `item` is event 7.
    public static void register(RegisterEvent event) {
        // Sounds are registry event 1, so they are in place before the ITEM window (event 7) builds the
        // shields — `ArsenalShields.<clinit>` reads `ArsenalSounds.shield_equip.entry()`.
        event.register(RegistryKeys.SOUND_EVENT, helper -> {
            SpellEngineSounds.soundsToRegister(ArsenalSounds.entries).forEach(helper::register);
            // The helper returns void where `Registry.registerReference` returned the entry; the shield
            // materials hold `Entry#entry()`, so link it back out of the registry here.
            SpellEngineSounds.linkEntries(ArsenalSounds.entries);
        });

        event.register(RegistryKeys.STATUS_EFFECT, helper -> {
            ArsenalEffects.configureEffects();
            Effects.effectsToRegister(ArsenalEffects.entries, ArsenalMod.effectConfig.value.effects)
                    .forEach(helper::register);
            // Nothing in Arsenal reads `Effects.Entry#entry`, but the field is public API for consumers
            // and `Effects.register` fills it on Fabric — keep the two paths equivalent.
            Effects.linkEntries(ArsenalEffects.entries);
            ArsenalMod.effectConfig.save();
        });

        // `Item`'s constructor takes an intrusive registry holder on 1.20.1, so the weapons, bows and
        // shields are built here, inside the ITEM window, not earlier.
        event.register(RegistryKeys.ITEM, helper -> {
            Weapon.itemsToRegister(ArsenalMod.itemConfig.value.weapons, ArsenalWeapons.entries, Group.KEY)
                    .forEach(helper::register);
            ArsenalMod.itemConfig.save();
            RangedWeapon.itemsToRegister(ArsenalMod.rangedConfig.value.ranged_weapons, ArsenalBows.entries, Group.KEY)
                    .forEach(helper::register);
            ArsenalMod.rangedConfig.save();
            Shield.itemsToRegister(ArsenalMod.shieldConfig.value.shields, ArsenalShields.entries, Group.KEY, CustomShieldItem::new)
                    .forEach(helper::register);
            ArsenalMod.shieldConfig.save();
        });

        // `creative_mode_tab` is event 65, 58 events after `item` — its own window, or the write is dropped.
        event.register(RegistryKeys.ITEM_GROUP, helper -> {
            ArsenalMod.createItemGroup();
            helper.register(Group.ID, Group.GROUP);
        });
    }
}
