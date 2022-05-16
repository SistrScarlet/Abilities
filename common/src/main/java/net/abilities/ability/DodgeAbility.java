package net.abilities.ability;

import net.abilities.util.HasMoveInput;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class DodgeAbility extends Ability.Base {
    protected int maxCool = 20;
    protected int cool = 0;

    public DodgeAbility(String name, LivingEntity entity) {
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
        if (entity instanceof HasMoveInput input) {
            Vec3d inputMove = input.getMoveInputVec();
            cool = maxCool;

            World world = entity.world;

            float range = 2f;

            BlockPos dodgeAtPos = new BlockPos(entity.getPos().add(inputMove.multiply(1, 0, 1).normalize().multiply(range)));

            Vec3d dodgeAtVec;

            Box box = new Box(
                    dodgeAtPos.getX() - entity.getWidth(),
                    dodgeAtPos.getY(),
                    dodgeAtPos.getZ() - entity.getWidth(),
                    dodgeAtPos.getX() + entity.getWidth(),
                    dodgeAtPos.getY() + entity.getHeight(),
                    dodgeAtPos.getZ() + entity.getWidth());
            int count = 0;
            int checkHeight = 5;
            while (true) {
                int i = MathHelper.ceil(count / 2.0f) * (count % 2 == 0 ? 1 : -1);
                BlockPos tmp = dodgeAtPos.offset(Direction.Axis.Y, i);
                //着地可、空間アリ
                if (world.getBlockState(tmp).isSideSolid(world, tmp, Direction.UP, SideShapeType.CENTER)
                        && world.isSpaceEmpty(box.offset(0, i, 0))) {
                    dodgeAtVec = new Vec3d(
                            dodgeAtPos.getX() + 0.5,
                            dodgeAtPos.getY() + i,
                            dodgeAtPos.getZ() + 0.5);
                    break;
                }
                count++;
                if (checkHeight * 2 < count) {
                    dodgeAtVec = new Vec3d(
                            dodgeAtPos.getX() + 0.5,
                            dodgeAtPos.getY(),
                            dodgeAtPos.getZ() + 0.5);
                    break;
                }
            }

            /*
             * 1000%忘れると思うので立式をメモ
             * 1,速度の変化量の式を立てる a=加速度 v=速度 k=抵抗の係数 t=時間
             * dv(t)/dt = a - kv
             * dv(t)/dt = -k(v - a/k)
             * 3,変数分離。つまり左にv、右にtを置く
             * dv(t) / (v - a / k) = -k * dt
             * 4,積分。左は分数の積分
             * log|v - a / k| = -kt + C
             * 5,変形する。e^CはDに置き換え
             * v - a / k = e^(-kt + C)
             * v = De^(-kt) + a/k
             * 6,e^Cから置き換えていたDに、D = v0 - a / kを代入(t=0の時、v=0にするため)
             * v = (v0 - a / k) e ^ (-kt) + a / k
             * 7,できた速度の式を積分
             * dx / dt = (v0 - a / k) e ^ (-kt) + a / k
             * x = -(v0 - a / k) e ^ (-kt) / k + at / k + C
             * 8,Cに(v0 - a / k) / kを代入(t=0の時、x=0にするため)
             * x = -(v0 - a / k) e ^ (-kt) / k + at / k + (v0 - a / k) / k
             * 9,できた位置の式を変形！完成！
             * v0 = a (e ^ (kt) (1 - kt) - 1) + k ^ 2 x e ^ (kt) / k ( e ^ (kt) - 1)
             * */

            Vec3d dodgeFor = dodgeAtVec.subtract(entity.getPos()).add(0, 0.5, 0);
            dodgeFor = dodgeFor.add(0, 1, 0);
            //m / s * 20
            double tick = dodgeFor.length() / 20 * 20;
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

            Vec3d velocity = dodgeFor.
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
}
