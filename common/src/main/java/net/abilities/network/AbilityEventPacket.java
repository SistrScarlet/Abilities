package net.abilities.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.abilities.AbilitiesMod;
import net.abilities.HasAbilities;
import net.abilities.ability.Ability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AbilityEventPacket {
    public static final Identifier ID = new Identifier(AbilitiesMod.MOD_ID, "ability_event");

    public static void sendC2S(String name) {
        var buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(name);
        NetworkManager.sendToServer(ID, buf);
    }

    public static void receiveC2S(PacketByteBuf packetByteBuf, NetworkManager.PacketContext packetContext) {
        String name = packetByteBuf.readString();
        packetContext.queue(() -> applyC2S(packetContext.getPlayer(), name));
    }

    public static void applyC2S(PlayerEntity player, String name) {
        ((HasAbilities) player).getAbility_abilities(name).ifPresent(Ability::event);
    }
}
