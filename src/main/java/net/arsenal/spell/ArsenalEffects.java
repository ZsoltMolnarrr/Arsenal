package net.arsenal.spell;

import net.spell_engine.api.config.ConfigFile;
import net.spell_engine.api.effect.Effects;
import net.spell_engine.api.effect.Synchronized;

import java.util.ArrayList;
import java.util.List;

public class ArsenalEffects {
    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static void register(ConfigFile.Effects config) {
        for (var entry: entries) {
            Synchronized.configure(entry.effect, true);
        }

        Effects.register(entries, config.effects);
    }
}
