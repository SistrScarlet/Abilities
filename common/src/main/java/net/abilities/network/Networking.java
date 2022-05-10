package net.abilities.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;

public class Networking {

    public static void init() {
        commonInit();
        if (Platform.getEnv() == EnvType.CLIENT) {
            clientInit();
        }
    }

    private static void commonInit() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, AbilityEventPacket.ID, AbilityEventPacket::receiveC2S);
    }

    private static void clientInit() {

    }
}
