package net.arsenal.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.arsenal.ArsenalMod;

import java.util.ArrayList;
import java.util.List;

public class ArsenalItemTags {
    public static final TagKey<Item> ALL = TagKey.of(RegistryKeys.ITEM, Identifier.of(ArsenalMod.NAMESPACE, "all"));

    // MARK: Repair tags

    /// One anvil-repair tag per *material*, shared by every item repaired with it.
    ///
    /// `Item.Settings.repairable(TagKey)` binds a live tag handle, so contents are read at anvil time —
    /// datapacks can override these. Where a vanilla tag already holds exactly the right item
    /// (`ItemTags.DIAMOND_TOOL_MATERIALS`, `ItemTags.GOLD_TOOL_MATERIALS`, ...) it is used directly instead.
    ///
    /// Contents are emitted by `ArsenalDataGen.ItemTagGenerator`.
    public record RepairTag(TagKey<Item> tag, List<Identifier> required, List<Identifier> optional) {}

    public static final List<RepairTag> REPAIR_TAGS = new ArrayList<>();

    private static TagKey<Item> repairs(String material, Identifier... items) {
        var tag = TagKey.of(RegistryKeys.ITEM, Identifier.of(ArsenalMod.NAMESPACE, "repairs_" + material));
        REPAIR_TAGS.add(new RepairTag(tag, List.of(items), List.of()));
        return tag;
    }

    public static final TagKey<Item> REPAIRS_GOLD_BLOCK = repairs("gold_block", Identifier.ofVanilla("gold_block"));
    public static final TagKey<Item> REPAIRS_IRON_BLOCK = repairs("iron_block", Identifier.ofVanilla("iron_block"));
    public static final TagKey<Item> REPAIRS_AMETHYST_BLOCK = repairs("amethyst_block", Identifier.ofVanilla("amethyst_block"));
    public static final TagKey<Item> REPAIRS_MAGMA_BLOCK = repairs("magma_block", Identifier.ofVanilla("magma_block"));
    public static final TagKey<Item> REPAIRS_BONE_BLOCK = repairs("bone_block", Identifier.ofVanilla("bone_block"));
    public static final TagKey<Item> REPAIRS_NETHERITE_SCRAP = repairs("netherite_scrap", Identifier.ofVanilla("netherite_scrap"));
    public static final TagKey<Item> REPAIRS_PRISMARINE = repairs("prismarine", Identifier.ofVanilla("prismarine"));
}
