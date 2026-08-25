package net.arsenal.fabric.datagen;

import net.arsenal.item.*;
import net.arsenal.spell.ArsenalSpellGroups;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.arsenal.ArsenalMod;
import net.arsenal.spell.ArsenalEffects;
import net.arsenal.spell.ArsenalSounds;
import net.arsenal.spell.ArsenalSpells;
import net.spell_engine.api.datagen.SimpleSoundGeneratorV2;
import net.spell_engine.api.datagen.SpellGenerator;
import net.spell_engine.api.datagen.WeaponAttributeGenerator;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.datagen.WeaponSkills;

import java.util.concurrent.CompletableFuture;

public class ArsenalDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(SpellTagGenerator::new);
        pack.addProvider(LangGenerator::new);
        pack.addProvider(SpellGen::new);
        pack.addProvider(SoundGen::new);
        pack.addProvider(WeaponGen::new);
    }

    public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
        public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            var all = builder(ArsenalItemTags.ALL);
            ArsenalWeapons.entries.forEach(entry -> all.addOptional(RegistryKey.of(RegistryKeys.ITEM, entry.id())));
            generateWeaponTags(ArsenalWeapons.entries);

            var bowEntries = ArsenalBows.entries.stream().map(entry ->
                    new RPGSeriesDataGen.BowEntry(entry.id(), entry.category, entry.lootProperties)
            ).toList();
            generateBowTags(bowEntries);

            var shieldEntries = ArsenalShields.entries.stream().map(entry ->
                    new RPGSeriesDataGen.ShieldEntry(entry.id(), entry.lootProperties)
            ).toList();
            generateShieldTags(shieldEntries);
        }
    }

    public static class SpellTagGenerator extends FabricTagProvider<Spell> {
        public SpellTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, SpellRegistry.KEY, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            ArsenalSpells.all.forEach(entry -> {
                for (var category: entry.categories()) {
                    var tagKey = TagKey.of(SpellRegistry.KEY, Identifier.of(ArsenalMod.NAMESPACE, category.toString().toLowerCase()));
                    var tag = builder(tagKey);
                    tag.addOptional(RegistryKey.of(SpellRegistry.KEY, entry.id()));
                }
            });

            // 1.21.6: `addOptionalTag` takes a `TagKey`, not an `Identifier`.
            var arcane = TagKey.of(SpellRegistry.KEY, Identifier.of("wizards", "weapon/arcane_staff"));
            var fire = TagKey.of(SpellRegistry.KEY, Identifier.of("wizards", "weapon/fire_staff"));
            var frost = TagKey.of(SpellRegistry.KEY, Identifier.of("wizards", "weapon/frost_staff"));
            var wizard = TagKey.of(SpellRegistry.KEY, Identifier.of("wizards", "weapon/wizard_staff"));
            var holy = TagKey.of(SpellRegistry.KEY, Identifier.of("paladins", "weapon/holy_staff"));

            builder(ArsenalSpellGroups.STAFF_ARCANE_FIRE)
                    .addOptionalTag(arcane)
                    .addOptionalTag(fire);
            builder(ArsenalSpellGroups.STAFF_ARCANE_HEALING)
                    .addOptionalTag(arcane)
                    .addOptionalTag(holy);
            builder(ArsenalSpellGroups.STAFF_ARCANE_FROST)
                    .addOptionalTag(arcane)
                    .addOptionalTag(frost);
            builder(ArsenalSpellGroups.STAFF_FIRE_FROST)
                    .addOptionalTag(fire)
                    .addOptionalTag(frost);
            builder(ArsenalSpellGroups.STAFF_ARCANE_FIRE_FROST)
                    .addOptionalTag(wizard);
            builder(ArsenalSpellGroups.STAFF_HEALING)
                    .addOptionalTag(holy);

            builder(ArsenalSpellGroups.ONE_HANDED_SLASHER)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.SWIFT_STRIKES.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.CLEAVE.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.SWIPE.id()));

            builder(ArsenalSpellGroups.TWO_HANDED_SLASHER)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.FLURRY.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.WHIRLWIND.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.THRUST.id()));

            builder(ArsenalSpellGroups.CLAYMORE_HAMMER)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.FLURRY.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.GROUND_SLAM.id()));
            builder(ArsenalSpellGroups.CLAYMORE_DOUBLE_AXE)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.FLURRY.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.WHIRLWIND.id()));
            builder(ArsenalSpellGroups.CLAYMORE_GLAIVE)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.FLURRY.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.THRUST.id()));
            builder(ArsenalSpellGroups.SPEAR_GLAIVE)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.IMPALE.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.THRUST.id()));
            builder(ArsenalSpellGroups.DAGGER_SICKLE)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.FAN_OF_KNIVES.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.SWIPE.id()));
            builder(ArsenalSpellGroups.SICKLE_AXE)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.SWIPE.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.CLEAVE.id()));
            builder(ArsenalSpellGroups.DOUBLE_AXE_HAMMER)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.GROUND_SLAM.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.WHIRLWIND.id()));
            builder(ArsenalSpellGroups.GLAIVE_DOUBLE_AXE)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.THRUST.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.WHIRLWIND.id()));
            builder(ArsenalSpellGroups.MACE_SWORD)
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.SMASH.id()))
                    .addOptional(RegistryKey.of(SpellRegistry.KEY, WeaponSkills.SWIFT_STRIKES.id()));
        }
    }

    public static class LangGenerator extends FabricLanguageProvider {
        protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, "en_us", registryLookup);
        }

        @Override
        public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
            translationBuilder.add(Group.translationKey, "Arsenal");
            ArsenalWeapons.entries.forEach(entry ->
                translationBuilder.add(entry.item().getTranslationKey(), entry.translatedName())
            );
            ArsenalBows.entries.forEach(entry ->
                translationBuilder.add(entry.item().getTranslationKey(), entry.translatedName())
            );
            ArsenalShields.entries.forEach(entry ->
                translationBuilder.add(entry.translationKey(), entry.translatedName())
            );
            ArsenalSpells.all.forEach(entry -> {
                var id = entry.id();
                translationBuilder.add("spell." + id.getNamespace() + "." + id.getPath() + ".name" , entry.title());
                translationBuilder.add("spell." + id.getNamespace() + "." + id.getPath() + ".description" , entry.description());
            });
            ArsenalEffects.entries.forEach(entry -> {
                translationBuilder.add(entry.effect.getTranslationKey(), entry.title);
                translationBuilder.add(entry.effect.getTranslationKey() + ".description", entry.description);
            });
        }
    }

    public static class SpellGen extends SpellGenerator {
        public SpellGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSpells(Builder builder) {
            for (var entry: ArsenalSpells.all) {
                builder.add(entry.id(), entry.spell());
            }
        }
    }

    public static class SoundGen extends SimpleSoundGeneratorV2 {
        public SoundGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSounds(Builder builder) {
            builder.entries.add(new Entry(ArsenalMod.NAMESPACE,
                            ArsenalSounds.entries.stream()
                                    .map(entry -> SoundEntry.withVariants(entry.id().getPath(), entry.variants()))
                                    .toList()
                    )
            );
        }
    }

    public static class WeaponGen extends WeaponAttributeGenerator {
        public WeaponGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateWeaponAttributes(Builder builder) {
            ArsenalWeapons.entries.forEach(entry -> {
                if (entry.weaponAttributesPreset != null && !entry.weaponAttributesPreset.isEmpty()) {
                    builder.entries.add(new Entry(entry.id(), entry.weaponAttributesPreset));
                }
            });
            ArsenalBows.entries.forEach(entry -> {
                if (entry.weaponAttributesPreset != null && !entry.weaponAttributesPreset.isEmpty()) {
                    builder.entries.add(new Entry(entry.id(), entry.weaponAttributesPreset));
                }
            });
        }
    }
}
