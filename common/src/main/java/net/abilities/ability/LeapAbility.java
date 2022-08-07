package net.abilities.ability;

import net.abilities.util.LeapCalc;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class LeapAbility extends Ability.Base {
    protected int maxCool = 20;
    protected int cool = 0;

    public LeapAbility(String name, LivingEntity entity) {
        super(name, entity);
    }

    @Override
    public void tick() {
        var entity = getEntity();
        if (entity.world.isClient) {
            return;
        }
        cool--;
    }

    @Override
    public void event() {
        var entity = getEntity();
        if (entity.world.isClient) {
            return;
        }
        if (0 < cool || !entity.isOnGround()) {
            return;
        }
        cool = maxCool;

        World world = entity.world;

        float range = 10f;

        Vec3d start = entity.getCameraPosVec(1f);
        Vec3d end = start.add(entity.getRotationVector().multiply(range));
        BlockPos lookAtPos;
        BlockHitResult bResult = world.raycast(
                new RaycastContext(start, end,
                        RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, entity));
        if (bResult.getType() != HitResult.Type.MISS) {
            lookAtPos = bResult.getBlockPos();
        } else {
            return;
        }

        Vec3d lookAtVec;

        Box box = new Box(
                lookAtPos.getX() - entity.getWidth(),
                lookAtPos.getY(),
                lookAtPos.getZ() - entity.getWidth(),
                lookAtPos.getX() + entity.getWidth(),
                lookAtPos.getY() + entity.getHeight(),
                lookAtPos.getZ() + entity.getWidth());
        int count = 0;
        while (true) {
            if (world.isSpaceEmpty(box.offset(0, count, 0))) {
                lookAtVec = new Vec3d(lookAtPos.getX() + 0.5, lookAtPos.getY() + count, lookAtPos.getZ() + 0.5);
                break;
            }
            count++;
            if (range / 2 <= count) {
                lookAtVec = new Vec3d(lookAtPos.getX() + 0.5, lookAtPos.getY(), lookAtPos.getZ() + 0.5);
                break;
            }
        }
        //水平方向に10より大きい、視認地点が現在地点-10より小さいか、現在地点+5以上
        if (range * range < lookAtVec
                .subtract(start)
                .multiply(1, 0, 1)
                .lengthSquared()
                || lookAtVec.getY() < entity.getY() - range || entity.getY() + range / 2 < lookAtVec.getY()) {
            return;
        }

        Vec3d leapFor = lookAtVec.subtract(start).add(0, 2, 0);
        leapFor = leapFor.add(0, 1, 0);

        double tick = leapFor.length() / 20 * 20;
        double gravity = -0.08f;

        Vec3d velocity = LeapCalc.calc(leapFor, tick, gravity);

        if (velocity.y < gravity * 4) {
            velocity = velocity.subtract(0, velocity.y, 0).add(0, gravity * 4, 0);
        }
        if (3.9 * 3.9 < velocity.lengthSquared()) {
            return;
        }
        entity.setVelocity(velocity);
        entity.velocityDirty = true;
        entity.velocityModified = true;
        entity.fallDistance -= 20f;
    }
}
