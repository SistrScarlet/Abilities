package net.abilities.setup;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.abilities.client.AbilitiesKeys;
import net.abilities.network.MoveInputPacket;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;

public class ClientSetup {

    public static void init() {
        AbilitiesKeys.init();
        ClientTickEvent.CLIENT_PRE.register(c -> AbilitiesKeys.tick());
        ClientTickEvent.CLIENT_PRE.register(c -> {
            if (c.player != null) {
                GameOptions options = c.options;
                MoveInputPacket.sendC2S(
                        options.jumpKey.isPressed(),
                        options.sneakKey.isPressed(),
                        options.forwardKey.isPressed(),
                        options.backKey.isPressed(),
                        options.leftKey.isPressed(),
                        options.rightKey.isPressed()
                );
            }
        });
        EntityRendererRegistry.register(Registration.BLAZE_FIRE, FallingBlockEntityRenderer::new);
    }
}
