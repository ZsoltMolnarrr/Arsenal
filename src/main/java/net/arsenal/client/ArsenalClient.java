package net.arsenal.client;

import net.fabricmc.api.ClientModInitializer;
import net.arsenal.spell.ArsenalSpells;
import net.spell_engine.client.gui.SpellTooltip;

public class ArsenalClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for (var entry: ArsenalSpells.entries) {
            if (entry.mutator() != null) {
                SpellTooltip.addDescriptionMutator(entry.id(), entry.mutator());
            }
        }
    }
}
