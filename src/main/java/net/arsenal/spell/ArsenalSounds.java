package net.arsenal.spell;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.arsenal.ArsenalMod;

import java.util.ArrayList;
import java.util.List;

public class ArsenalSounds {
    public record Entry(String name) {
        public Identifier id() {
            return Identifier.of(ArsenalMod.NAMESPACE, name);
        }
    }
    public static final List<Entry> entries = new ArrayList<>();
    public static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    // public static final Entry SHARPEN = add(new Entry("sharpen"));

    public static void register() {
        for (var entry: entries) {
            var soundId = entry.id();
            var soundEvent = SoundEvent.of(soundId);
            Registry.register(Registries.SOUND_EVENT, soundId, soundEvent);
        }
    }
}
