package net.arsenal.datagen;

import net.arsenal.item.*;
import net.arsenal.spell.ArsenalSpellGroups;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
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
        pack.addProvider(ModelProvider::new);
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
            var all = getOrCreateTagBuilder(ArsenalItemTags.ALL);
            ArsenalWeapons.entries.forEach(entry -> all.addOptional(entry.id()));
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
                    var tag = getOrCreateTagBuilder(tagKey);
                    tag.addOptional(entry.id());
                }
            });

            var arcane = Identifier.of("wizards", "weapon/arcane_staff");
            var fire = Identifier.of("wizards", "weapon/fire_staff");
            var frost = Identifier.of("wizards", "weapon/frost_staff");
            var wizard = Identifier.of("wizards", "weapon/wizard_staff");
            var holy = Identifier.of("paladins", "weapon/holy_staff");

            getOrCreateTagBuilder(ArsenalSpellGroups.STAFF_ARCANE_FIRE)
                    .addOptionalTag(arcane)
                    .addOptionalTag(fire);
            getOrCreateTagBuilder(ArsenalSpellGroups.STAFF_ARCANE_HEALING)
                    .addOptionalTag(arcane)
                    .addOptionalTag(holy);
            getOrCreateTagBuilder(ArsenalSpellGroups.STAFF_ARCANE_FROST)
                    .addOptionalTag(arcane)
                    .addOptionalTag(frost);
            getOrCreateTagBuilder(ArsenalSpellGroups.STAFF_FIRE_FROST)
                    .addOptionalTag(fire)
                    .addOptionalTag(frost);
            getOrCreateTagBuilder(ArsenalSpellGroups.STAFF_ARCANE_FIRE_FROST)
                    .addOptionalTag(wizard);
            getOrCreateTagBuilder(ArsenalSpellGroups.STAFF_HEALING)
                    .addOptionalTag(holy);

            getOrCreateTagBuilder(ArsenalSpellGroups.ONE_HANDED_SLASHER)
                    .addOptional(WeaponSkills.SWIFT_STRIKES.id())
                    .addOptional(WeaponSkills.CLEAVE.id())
                    .addOptional(WeaponSkills.SWIPE.id());

            getOrCreateTagBuilder(ArsenalSpellGroups.TWO_HANDED_SLASHER)
                    .addOptional(WeaponSkills.FLURRY.id())
                    .addOptional(WeaponSkills.WHIRLWIND.id())
                    .addOptional(WeaponSkills.THRUST.id());

            getOrCreateTagBuilder(ArsenalSpellGroups.CLAYMORE_HAMMER)
                    .addOptional(WeaponSkills.FLURRY.id())
                    .addOptional(WeaponSkills.GROUND_SLAM.id());
            getOrCreateTagBuilder(ArsenalSpellGroups.CLAYMORE_DOUBLE_AXE)
                    .addOptional(WeaponSkills.FLURRY.id())
                    .addOptional(WeaponSkills.WHIRLWIND.id());
            getOrCreateTagBuilder(ArsenalSpellGroups.CLAYMORE_GLAIVE)
                    .addOptional(WeaponSkills.FLURRY.id())
                    .addOptional(WeaponSkills.THRUST.id());
            getOrCreateTagBuilder(ArsenalSpellGroups.SPEAR_GLAIVE)
                    .addOptional(WeaponSkills.IMPALE.id())
                    .addOptional(WeaponSkills.THRUST.id());
            getOrCreateTagBuilder(ArsenalSpellGroups.DAGGER_SICKLE)
                    .addOptional(WeaponSkills.FAN_OF_KNIVES.id())
                    .addOptional(WeaponSkills.SWIPE.id());
            getOrCreateTagBuilder(ArsenalSpellGroups.SICKLE_AXE)
                    .addOptional(WeaponSkills.SWIPE.id())
                    .addOptional(WeaponSkills.CLEAVE.id());
            getOrCreateTagBuilder(ArsenalSpellGroups.DOUBLE_AXE_HAMMER)
                    .addOptional(WeaponSkills.GROUND_SLAM.id())
                    .addOptional(WeaponSkills.WHIRLWIND.id());
            getOrCreateTagBuilder(ArsenalSpellGroups.GLAIVE_DOUBLE_AXE)
                    .addOptional(WeaponSkills.THRUST.id())
                    .addOptional(WeaponSkills.WHIRLWIND.id());
            getOrCreateTagBuilder(ArsenalSpellGroups.MACE_SWORD)
                    .addOptional(WeaponSkills.SMASH.id())
                    .addOptional(WeaponSkills.SWIFT_STRIKES.id());
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

    public static class ModelProvider extends FabricModelProvider {
        public ModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
//            RelicItems.entries.forEach(entry -> {
//                itemModelGenerator.register(entry.item().get(), Models.GENERATED);
//            });
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
