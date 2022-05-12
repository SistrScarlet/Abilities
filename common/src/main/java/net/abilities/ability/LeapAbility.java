package net.abilities.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class LeapAbility extends Ability.Base {
    protected int maxCool = 20;
    protected int cool = 0;

    public LeapAbility(String name, LivingEntity entity) {
        super(name, entity);
    }

    @Override
    public void tick() {
        cool--;
    }

    @Override
    public void event() {
        if (0 < cool) {
            return;
        }
        cool = maxCool;

        LivingEntity entity = getEntity();

        Vec3d start = entity.getCameraPosVec(1f);
        Vec3d end = start.add(entity.getRotationVector().multiply(16));
        Vec3d lookAt = end;
        BlockHitResult bResult = entity.world.raycast(
                new RaycastContext(start, end,
                        RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, entity));
        if (bResult.getType() != HitResult.Type.MISS) {
            lookAt = bResult.getPos();
        }

        Vec3d leapFor = lookAt.subtract(start);
        float tick = (float) (leapFor.length() / 5 * 20);
        float gravity = -0.08f;
        float vertK = 1 - 0.98f;
        float horiK = 1 - 0.91f;

        Vec3d velocity = leapFor;
        if (3.9 * 3.9 < velocity.lengthSquared()) {
            return;
        }
        entity.setVelocity(velocity);
        entity.velocityDirty = true;
        entity.velocityModified = true;
        entity.fallDistance -= 20f;


    }
}
