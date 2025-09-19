package net.arsenal.util;

import net.minecraft.util.Identifier;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;

import java.util.List;

public class SpellSchoolUtil {
    public static List<SpellSchool> allMagicSchools() {
        return magicSchools(List.of("generic"));
    }

    public static List<SpellSchool> allOffensiveMagicSchools() {
        return magicSchools(List.of("generic", "healing"));
    }

    public static List<SpellSchool> magicSchools(List<String> except) {
        return SpellSchools.all().stream()
                .filter(school -> school.archetype == SpellSchool.Archetype.MAGIC
                        && !isException(except, school.id))
                .toList();
    }

    private static boolean isException(List<String> exception, Identifier id) {
        for (var e: exception) {
            if (id.toString().contains(e)) {
                return true;
            }
        }
        return false;
    }
}
