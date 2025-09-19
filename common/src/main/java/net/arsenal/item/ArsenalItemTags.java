package net.arsenal.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.arsenal.ArsenalMod;

public class ArsenalItemTags {
    public static final TagKey<Item> ALL = TagKey.of(RegistryKeys.ITEM, Identifier.of(ArsenalMod.NAMESPACE, "all"));
}
