package net.arsenal.spell;

import net.arsenal.ArsenalMod;
import net.minecraft.util.Identifier;
import net.spell_engine.fx.SpellEngineSounds;

import java.util.ArrayList;
import java.util.List;

public class ArsenalSounds {
    public static final List<SpellEngineSounds.Entry> entries = new ArrayList<>();
    private static SpellEngineSounds.Entry add(SpellEngineSounds.Entry entry) {
        entries.add(entry);
        return entry;
    }
    private static SpellEngineSounds.Entry entry(String name) {
        return new SpellEngineSounds.Entry(Identifier.of(ArsenalMod.NAMESPACE, name));
    }

    public static final SpellEngineSounds.Entry shield_equip = add(entry("shield_equip").variants(3));
    public static final SpellEngineSounds.Entry wither_impact = add(entry("wither_impact"));
    public static final SpellEngineSounds.Entry poison_cloud_spawn = add(entry("poison_cloud_spawn"));
    public static final SpellEngineSounds.Entry poison_cloud_tick = add(entry("poison_cloud_tick"));
    public static final SpellEngineSounds.Entry leeching_impact = add(entry("leeching_impact"));
    public static final SpellEngineSounds.Entry guardian_strike_release = add(entry("guardian_strike_release"));
    public static final SpellEngineSounds.Entry guardian_strike_impact = add(entry("guardian_strike_impact"));
    public static final SpellEngineSounds.Entry guardian_heal_impact = add(entry("guardian_heal_impact"));
    public static final SpellEngineSounds.Entry missile_impact = add(entry("missile_impact"));
    public static final SpellEngineSounds.Entry missile_release = add(entry("missile_release"));
    public static final SpellEngineSounds.Entry sunder_impact = add(entry("sunder_impact"));
    public static final SpellEngineSounds.Entry unyielding_impact = add(entry("unyielding_impact"));
    public static final SpellEngineSounds.Entry shockwave_impact = add(entry("shockwave_impact"));
    public static final SpellEngineSounds.Entry shockwave_release = add(entry("shockwave_release"));
    public static final SpellEngineSounds.Entry spike_impact = add(entry("spike_impact"));
    public static final SpellEngineSounds.Entry spell_cooldown_impact = add(entry("spell_cooldown_impact"));
    public static final SpellEngineSounds.Entry swirling = add(entry("swirling"));
    public static final SpellEngineSounds.Entry radiance_impact = add(entry("radiance_impact"));
    public static final SpellEngineSounds.Entry rampaging_activate = add(entry("rampaging_activate"));
    public static final SpellEngineSounds.Entry focusing_activate = add(entry("focusing_activate"));
    public static final SpellEngineSounds.Entry surging_activate = add(entry("surging_activate"));

    public static void register() {
        for (var entry: entries) {
            entry.register();
        }
    }
}
