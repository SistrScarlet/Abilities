package net.abilities.mixin;

import com.mojang.authlib.GameProfile;
import net.abilities.ability.AbilityManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity extends MixinLivingEntity {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(World world, BlockPos pos, float yaw, GameProfile profile, CallbackInfo ci) {
        AbilityManager.INSTANCE.create("leap", (LivingEntity) (Object)this).ifPresent(this::addAbility);
        AbilityManager.INSTANCE.create("fly", (LivingEntity) (Object)this).ifPresent(this::addAbility);
    }

}
