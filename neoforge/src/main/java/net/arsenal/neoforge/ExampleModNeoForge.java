package net.arsenal.neoforge;

import net.neoforged.fml.common.Mod;

import net.arsenal.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
