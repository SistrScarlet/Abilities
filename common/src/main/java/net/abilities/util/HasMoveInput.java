package net.abilities.util;

import net.minecraft.util.math.Vec3d;

public interface HasMoveInput {
    Vec3d getMoveInputVec();

    void setMoveInput(Vec3d moveInput);
}
