package net.arsenal.item;

import net.arsenal.ArsenalMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import java.util.function.Supplier;

public class Group {
    public static Identifier ID = Identifier.fromNamespaceAndPath(ArsenalMod.NAMESPACE, "generic");
    public static String translationKey = "itemGroup." + ID.getNamespace() + "." + ID.getPath();
    public static ResourceKey<CreativeModeTab> KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ID);
    public static CreativeModeTab GROUP;
    public static Supplier<ItemStack> ICON = () -> {
        return new ItemStack( ArsenalWeapons.entries.get(0).item() );
    };
}
