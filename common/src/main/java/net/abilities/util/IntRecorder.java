package net.abilities.util;

import java.util.OptionalInt;

public class IntRecorder {
    private int record = 0;

    public void set(int index, boolean flag) {
        //微妙？
        if (flag) {
            record = record | (1 << index);
        } else {
            record = record & ~(1 << index);
        }
    }

    //flagがtrueなら、0->1を探し、falseなら1->0を探す
    public OptionalInt getChangeIndex(boolean toUp) {
        boolean prev = !toUp;
        for (int i = 0; i < 32; i++) {
            boolean check = getFlag(i);
            //checkが左の桁、prevが右の桁
            if (check == !toUp && prev == toUp) {
                return OptionalInt.of(i);
            }
            prev = check;
        }
        return OptionalInt.empty();
    }

    //フラグを記録
    public void push(boolean flag) {
        record = (record << 1) | toInt(flag);
    }

    //flagを取得
    public boolean getFlag(int index) {
        return ((record >> index) & 1) == 1;
    }

    //直近でflagだったindexを取得
    public OptionalInt getIndex(boolean flag) {
        for (int i = 0; i < 32; i++) {
            if (flag == getFlag(i)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    private int toInt(boolean flag) {
        return (flag ? 1 : 0);
    }
}
