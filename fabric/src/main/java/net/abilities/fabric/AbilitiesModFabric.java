package net.abilities.fabric;

import net.abilities.AbilitiesMod;
import net.abilities.setup.ClientSetup;
import net.abilities.setup.ModSetup;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class AbilitiesModFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        AbilitiesMod.init();
        ModSetup.init();
    }

    @Override
    public void onInitializeClient() {
        ClientSetup.init();
    }
}
