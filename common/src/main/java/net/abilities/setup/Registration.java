package net.abilities.setup;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.abilities.AbilitiesMod;
import net.abilities.entity.BlazeFireEntity;
import net.abilities.entity.effect.BlazeStatusEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.registry.Registry;

public class Registration {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(AbilitiesMod.MOD_ID, Registry.ENTITY_TYPE_KEY);
    private static final DeferredRegister<StatusEffect> EFFECTS = DeferredRegister.create(AbilitiesMod.MOD_ID, Registry.MOB_EFFECT_KEY);

    public static void init() {
        ENTITIES.register();
        EFFECTS.register();
    }

    public static final RegistrySupplier<EntityType<BlazeFireEntity>> BLAZE_FIRE = ENTITIES.register("blaze_fire", () ->
            EntityType.Builder.<BlazeFireEntity>create(BlazeFireEntity::new, SpawnGroup.MISC)
                    .setDimensions(0.98f, 0.98f)
                    .build("blaze_fire"));

    public static final RegistrySupplier<StatusEffect> BLAZE = EFFECTS.register("blaze",
            () -> new BlazeStatusEffect(StatusEffectCategory.HARMFUL, 0xFF4000));

}
