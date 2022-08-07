package net.abilities.util;

import net.minecraft.util.math.Vec3d;

public class LeapCalc {

    public static Vec3d calc(Vec3d leapFor, double tick, double gravity) {
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

        //m / s * 20
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

        return leapFor.multiply(mulHori, mulVert, mulHori)
                .add(0, gravity * (eKTVert * (1 - kTVert) - 1), 0)
                .multiply(divHori, divVert, divHori);
    }

}
