package net.abilities.ability;

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
        cool--;
    }

    @Override
    public void event() {
        LivingEntity entity = getEntity();
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
        //???????????????10?????????????????????????????????????????????-10?????????????????????????????????+5??????
        if (range * range < lookAtVec
                .subtract(start)
                .multiply(1, 0, 1)
                .lengthSquared()
                || lookAtVec.getY() < entity.getY() - range || entity.getY() + range / 2 < lookAtVec.getY()) {
            return;
        }

        /*
         * 1000%???????????????????????????????????????
         * 1,???????????????????????????????????? a=????????? v=?????? k=??????????????? t=??????
         * dv(t)/dt = a - kv
         * dv(t)/dt = -k(v - a/k)
         * 3,??????????????????????????????v?????????t?????????
         * dv(t) / (v - a / k) = -k * dt
         * 4,??????????????????????????????
         * log|v - a / k| = -kt + C
         * 5,???????????????e^C???D???????????????
         * v - a / k = e^(-kt + C)
         * v = De^(-kt) + a/k
         * 6,e^C???????????????????????????D??????D = v0 - a / k?????????(t=0?????????v=0???????????????)
         * v = (v0 - a / k) e ^ (-kt) + a / k
         * 7,??????????????????????????????
         * dx / dt = (v0 - a / k) e ^ (-kt) + a / k
         * x = -(v0 - a / k) e ^ (-kt) / k + at / k + C
         * 8,C???(v0 - a / k) / k?????????(t=0?????????x=0???????????????)
         * x = -(v0 - a / k) e ^ (-kt) / k + at / k + (v0 - a / k) / k
         * 9,??????????????????????????????????????????
         * v0 = a (e ^ (kt) (1 - kt) - 1) + k ^ 2 x e ^ (kt) / k ( e ^ (kt) - 1)
         * */

        Vec3d leapFor = lookAtVec.subtract(start).add(0, 2, 0);
        leapFor = leapFor.add(0, 1, 0);
        //m / s * 20
        double tick = leapFor.length() / 20 * 20;
        double gravity = -0.08f;
        double kVert = 1 - 0.98f;
        double kHori = 1 - 0.91f;
        double kTHori = kHori * tick;
        double kTVert = kHori * tick / 3;
        double eKTHori = Math.pow(Math.E, kTHori);
        double eKTVert = Math.pow(Math.E, kTVert);
        double mulHori = kHori * kHori * eKTHori;
        double mulVert = kVert * kVert * eKTVert;
        double divHori = 1 / (kHori * (eKTHori - 1));
        double divVert = 1 / (kVert * (eKTVert - 1));

        Vec3d velocity = leapFor.
                multiply(
                        mulHori,
                        mulVert,
                        mulHori)
                .add(0, gravity * (eKTVert * (1 - kTVert) - 1), 0)
                .multiply(divHori, divVert, divHori);
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
