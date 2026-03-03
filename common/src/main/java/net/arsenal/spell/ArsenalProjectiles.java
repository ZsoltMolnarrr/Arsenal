package net.arsenal.spell;

import net.arsenal.ArsenalMod;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class ArsenalProjectiles {
    public record Entry(Identifier id) { }
    public static final ArrayList<Entry> all = new ArrayList<>();
    public static Entry entry(String name) {
        var entry = new Entry(Identifier.of(ArsenalMod.NAMESPACE, "spell_projectile/" + name));
        all.add(entry);
        return entry;
    }

    public static final Entry shockwave = entry("shockwave");
    public static final Entry shockwave_large = entry("shockwave_large");
    public static final Entry missile = entry("missile");
}
