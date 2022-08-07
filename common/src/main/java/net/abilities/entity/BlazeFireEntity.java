package net.abilities.entity;

import net.abilities.entity.effect.BlazeStatusEffect;
import net.abilities.mixin.FallingBlockEntityAccessor;
import net.abilities.setup.Registration;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlazeFireEntity extends FallingBlockEntity {
    @Nullable
    protected LivingEntity owner;

    public BlazeFireEntity(EntityType<? extends BlazeFireEntity> entityType, World world) {
        super(entityType, world);
    }

    public BlazeFireEntity(World world, @Nullable LivingEntity owner) {
        super(Registration.BLAZE_FIRE.get(), world);
        var fire = Blocks.FIRE.getDefaultState();
        fire = fire.with(FireBlock.AGE, 12);
        ((FallingBlockEntityAccessor) this).setBlockState(fire);

        if (owner != null) {
            setOwner(owner);
            this.setFallingBlockPos(this.getBlockPos());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            var entities = this.world.getOtherEntities(this, getBoundingBox(),
                    e -> (owner == null || e != owner && !owner.isConnectedThroughVehicle(e))
                            && !e.isSpectator() && e.isAlive() && e.collides()
                            && (!(e instanceof TameableEntity)
                            || !owner.getUuid().equals(((TameableEntity) e).getOwnerUuid())));
            entities.forEach(target -> {
                target.setFireTicks(20);
                if (target instanceof LivingEntity) {
                    ((LivingEntity) target).addStatusEffect(BlazeStatusEffect.create(10, 0), owner);
                }
            });
        }
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
    }

    @Nullable
    public LivingEntity getOwner() {
        return owner;
    }
}
