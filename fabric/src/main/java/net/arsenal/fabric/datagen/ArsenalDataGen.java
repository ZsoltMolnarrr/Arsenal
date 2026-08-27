package net.arsenal.fabric.datagen;

import net.arsenal.item.*;
import net.arsenal.spell.ArsenalSpellGroups;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
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
        public ItemTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            var all = builder(ArsenalItemTags.ALL);
            ArsenalWeapons.entries.forEach(entry -> all.addOptional(ResourceKey.create(Registries.ITEM, entry.id())));
            generateWeaponTags(ArsenalWeapons.entries);

            var bowEntries = ArsenalBows.entries.stream().map(entry ->
                    new RPGSeriesDataGen.BowEntry(entry.id(), entry.category, entry.lootProperties)
            ).toList();
            generateBowTags(bowEntries);

            var shieldEntries = ArsenalShields.entries.stream().map(entry ->
                    new RPGSeriesDataGen.ShieldEntry(entry.id(), entry.lootProperties)
            ).toList();
            generateShieldTags(shieldEntries);

            // Anvil repair tags (`minecraft:repairable`), one per material
            for (var repair: ArsenalItemTags.REPAIR_TAGS) {
                var tag = builder(repair.tag());
                repair.required().forEach(id -> tag.add(ResourceKey.create(Registries.ITEM, id)));
                repair.optional().forEach(id -> tag.addOptional(ResourceKey.create(Registries.ITEM, id)));
            }
        }
    }

    public static class SpellTagGenerator extends FabricTagsProvider<Spell> {
        public SpellTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, SpellRegistry.KEY, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            ArsenalSpells.all.forEach(entry -> {
                for (var category: entry.categories()) {
                    var tagKey = TagKey.create(SpellRegistry.KEY, Identifier.fromNamespaceAndPath(ArsenalMod.NAMESPACE, category.toString().toLowerCase()));
                    var tag = builder(tagKey);
                    tag.addOptional(ResourceKey.create(SpellRegistry.KEY, entry.id()));
                }
            });

            // 1.21.6: `addOptionalTag` takes a `TagKey`, not an `Identifier`.
            var arcane = TagKey.create(SpellRegistry.KEY, Identifier.fromNamespaceAndPath("wizards", "weapon/arcane_staff"));
            var fire = TagKey.create(SpellRegistry.KEY, Identifier.fromNamespaceAndPath("wizards", "weapon/fire_staff"));
            var frost = TagKey.create(SpellRegistry.KEY, Identifier.fromNamespaceAndPath("wizards", "weapon/frost_staff"));
            var wizard = TagKey.create(SpellRegistry.KEY, Identifier.fromNamespaceAndPath("wizards", "weapon/wizard_staff"));
            var holy = TagKey.create(SpellRegistry.KEY, Identifier.fromNamespaceAndPath("paladins", "weapon/holy_staff"));

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
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.SWIFT_STRIKES.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.CLEAVE.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.SWIPE.id()));

            builder(ArsenalSpellGroups.TWO_HANDED_SLASHER)
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.FLURRY.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.WHIRLWIND.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.THRUST.id()));

            builder(ArsenalSpellGroups.CLAYMORE_HAMMER)
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.FLURRY.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.GROUND_SLAM.id()));
            builder(ArsenalSpellGroups.CLAYMORE_DOUBLE_AXE)
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.FLURRY.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.WHIRLWIND.id()));
            builder(ArsenalSpellGroups.CLAYMORE_GLAIVE)
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.FLURRY.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.THRUST.id()));
            builder(ArsenalSpellGroups.SPEAR_GLAIVE)
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.IMPALE.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.THRUST.id()));
            builder(ArsenalSpellGroups.DAGGER_SICKLE)
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.FAN_OF_KNIVES.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.SWIPE.id()));
            builder(ArsenalSpellGroups.SICKLE_AXE)
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.SWIPE.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.CLEAVE.id()));
            builder(ArsenalSpellGroups.DOUBLE_AXE_HAMMER)
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.GROUND_SLAM.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.WHIRLWIND.id()));
            builder(ArsenalSpellGroups.GLAIVE_DOUBLE_AXE)
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.THRUST.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.WHIRLWIND.id()));
            builder(ArsenalSpellGroups.MACE_SWORD)
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.SMASH.id()))
                    .addOptional(ResourceKey.create(SpellRegistry.KEY, WeaponSkills.SWIFT_STRIKES.id()));
        }
    }

    public static class LangGenerator extends FabricLanguageProvider {
        protected LangGenerator(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, "en_us", registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider wrapperLookup, TranslationBuilder translationBuilder) {
            translationBuilder.add(Group.translationKey, "Arsenal");
            ArsenalWeapons.entries.forEach(entry ->
                translationBuilder.add(entry.item().getDescriptionId(), entry.translatedName())
            );
            ArsenalBows.entries.forEach(entry ->
                translationBuilder.add(entry.item().getDescriptionId(), entry.translatedName())
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
                translationBuilder.add(entry.effect.getDescriptionId(), entry.title);
                translationBuilder.add(entry.effect.getDescriptionId() + ".description", entry.description);
            });
        }
    }

    public static class SpellGen extends SpellGenerator {
        public SpellGen(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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
        public SoundGen(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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
        public WeaponGen(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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
