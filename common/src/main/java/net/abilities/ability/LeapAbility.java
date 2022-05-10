package net.abilities.ability;

import net.minecraft.entity.LivingEntity;

public class LeapAbility extends Ability.Base {

    public LeapAbility(String name, LivingEntity entity) {
        super(name, entity);
    }

    @Override
    public void tick() {

    }

    @Override
    public void event() {
        LivingEntity entity = getEntity();
        entity.setVelocity(entity.getVelocity()
                .multiply(1, 0, 1)
                .add(entity.getRotationVector().add(0, 0.5, 0)));
        entity.velocityDirty = true;
        entity.velocityModified = true;
        entity.fallDistance -= 20f;
    }
}
