package net.abilities.setup;

import net.abilities.ability.Abilities;
import net.abilities.network.Networking;

public class ModSetup {

    public static void init() {
        Networking.init();
        Abilities.init();
    }
}
