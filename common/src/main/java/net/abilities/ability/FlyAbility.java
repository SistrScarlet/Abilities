package net.abilities.ability;

import net.abilities.util.HasMoveInput;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class FlyAbility extends Ability.Base {
    protected int cool;
    protected boolean enable;
    protected boolean prevEnable;

    public FlyAbility(String name, LivingEntity entity) {
        super(name, entity);
    }

    @Override
    public void tick() {
        cool--;
        if (prevEnable != enable) {
            getEntity().setNoGravity(enable);
        }
        if (enable) {
            LivingEntity entity = getEntity();
            if (entity instanceof HasMoveInput input) {
                Vec3d moveInput = input.getMoveInputVec();
                float k = 0.9f;
                float airDrag = 0.91f;
                float spd = 4.5f;
                entity.setVelocity(entity.getVelocity()
                        .multiply(k)
                        .add(moveInput.multiply(spd / 20 * (1 - k * airDrag)))
                        .multiply(1, 0 < moveInput.y ? 0.75f : 1, 1));
                entity.fallDistance = 0;
                entity.velocityDirty = true;
                entity.velocityModified = true;
            }
        }
        prevEnable = enable;
    }

    @Override
    public void event() {
        if (cool < 0) {
            enable = !enable;
            cool = 4;
        }
    }
}
