package net.abilities.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.abilities.AbilitiesMod;
import net.abilities.util.HasMoveInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MoveInputPacket {
    public static final Identifier ID = new Identifier(AbilitiesMod.MOD_ID, "move_input");

    public static void sendC2S(boolean up, boolean down, boolean forward, boolean back, boolean left, boolean right) {
        var buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte((up ? 1 : 0) << 5 | (down ? 1 : 0) << 4
                | (forward ? 1 : 0) << 3 | (back ? 1 : 0) << 2
                | (left ? 1 : 0) << 1 | (right ? 1 : 0));
        NetworkManager.sendToServer(ID, buf);
    }

    public static void receiveC2S(PacketByteBuf packetByteBuf, NetworkManager.PacketContext packetContext) {
        byte b = packetByteBuf.readByte();
        boolean up = (b & (1 << 5)) != 0;
        boolean down = (b & (1 << 4)) != 0;
        boolean forward = (b & (1 << 3)) != 0;
        boolean back = (b & (1 << 2)) != 0;
        boolean left = (b & (1 << 1)) != 0;
        boolean right = (b & 1) != 0;
        packetContext.queue(() -> applyC2S(packetContext.getPlayer(), up, down, forward, back, left, right));
    }

    public static void applyC2S(PlayerEntity player,
                                boolean up, boolean down,
                                boolean forward, boolean back,
                                boolean left, boolean right) {
        float x = (right ? -1 : 0) + (left ? 1 : 0);
        float y = (down ? -1 : 0) + (up ? 1 : 0);
        float z = (back ? -1 : 0) + (forward ? 1 : 0);
        Vec3d vec = movementInputToVelocity(new Vec3d(x, y, z), player.getYaw());
        ((HasMoveInput)player).setMoveInput(vec);
    }

    private static Vec3d movementInputToVelocity(Vec3d movementInput, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput);
        float f = MathHelper.sin(yaw * ((float)Math.PI / 180));
        float g = MathHelper.cos(yaw * ((float)Math.PI / 180));
        return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
    }

}
