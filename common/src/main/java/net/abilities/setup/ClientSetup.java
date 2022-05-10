package net.abilities.setup;

import dev.architectury.event.events.client.ClientTickEvent;
import net.abilities.client.AbilitiesKeys;

public class ClientSetup {

    public static void init() {
        AbilitiesKeys.init();
        ClientTickEvent.CLIENT_PRE.register(c -> AbilitiesKeys.tick());
    }
}
