package net.abilities.client;

import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.abilities.AbilitiesMod;
import net.abilities.ability.AbilityManager;
import net.abilities.network.AbilityEventPacket;
import net.minecraft.client.option.KeyBinding;
import org.apache.commons.compress.utils.Lists;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class AbilitiesKeys {
    public static final List<KeySet> keys = Lists.newArrayList();

    public static void init() {
        AbilityManager.INSTANCE.getAbilityNames().forEach(name ->
                keys.add(new KeySet(name, new KeyBinding(
                        AbilitiesMod.MOD_ID + ".key.ability." + name,
                        GLFW.GLFW_KEY_GRAVE_ACCENT,
                        AbilitiesMod.MOD_ID + ".key.categories.ability"))));
        keys.forEach(keySet -> register(keySet.keyBinding));
    }

    public static void tick() {
        keys.forEach(keySet -> {
            if (keySet.keyBinding.isPressed()) {
                AbilityEventPacket.sendC2S(keySet.name);
            }
        });
    }

    private static void register(KeyBinding keyBinding) {
        KeyMappingRegistry.register(keyBinding);
    }

    public record KeySet(String name, KeyBinding keyBinding) {
    }

}
