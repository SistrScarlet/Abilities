package net.abilities.ability;

import net.abilities.entity.BlazeFireEntity;
import net.abilities.entity.effect.BlazeStatusEffect;
import net.abilities.mixin.DamageSourceAccessor;
import net.abilities.setup.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class BlazeAbility extends Ability.Base {
    protected boolean blazing;
    protected int cool;
    protected int extinguishTimer;
    protected boolean stomp;

    public BlazeAbility(String name, LivingEntity entity) {
        super(name, entity);
    }

    public boolean isBlazing() {
        return blazing;
    }

    @Override
    public void tick() {
        var owner = getEntity();
        if (owner.world.isClient) {
            return;
        }

        if (0 < cool) {
            cool--;
        }
        if (0 < extinguishTimer) {
            extinguishTimer--;
        }

        if (!blazing) {
            return;
        }

        //以下ブレイズ

        owner.fallDistance = 0;
        owner.setFireTicks(0);
        if (owner.age % 20 == 0) {
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, extinguishTimer, 2));
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, extinguishTimer, 1));
        }

        if (owner.isTouchingWaterOrRain()) {
            extinguishTimer = 0;
        }

        if (extinguishTimer <= 0) {
            //幻影対策
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1, 2));
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1, 1));
            owner.addStatusEffect(new StatusEffectInstance(Registration.BLAZE.get(), 1, 0));
            owner.removeStatusEffect(StatusEffects.SPEED);
            owner.removeStatusEffect(StatusEffects.RESISTANCE);
            owner.removeStatusEffect(Registration.BLAZE.get());
            extinguishTimer = 0;
            blazing = false;
            cool = 100;
            ((ServerWorld) owner.world).spawnParticles(ParticleTypes.FLAME,
                    owner.getX(), owner.getEyeY(), owner.getZ(),
                    20, 0, 1, 0, -0.5);
            owner.world.playSound(null, owner.getX(), owner.getEyeY(), owner.getZ(),
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return;
        }

        var rand = owner.getRandom();
        ((ServerWorld) owner.world).spawnParticles(ParticleTypes.FLAME,
                owner.getX(),
                owner.getY() + owner.getHeight() / 2,
                owner.getZ(),
                extinguishTimer < 40 ? 2 : 1,
                (rand.nextFloat() * 2 - 1),
                rand.nextFloat(),
                (rand.nextFloat() * 2 - 1),
                0.01);

        if (stomp) {
            if (owner.isOnGround()) {
                stomp = false;
                decreaseExtinguishTimer();
                blazeBoom(owner, owner);
                owner.world.getOtherEntities(owner, owner.getBoundingBox().expand(5),
                                e -> canAttack(e) && owner.squaredDistanceTo(e) < 5 * 5 && owner.canSee(e))
                        .forEach(target -> {
                            var damageSource = getDamageSource(owner);

                            owner.timeUntilRegen = 0;
                            if (target.damage(damageSource, 5)) {
                                extinguishTimer = 200;

                                var toFar = target.getPos().subtract(owner.getPos()).normalize()
                                        .multiply(0.5 * owner.distanceTo(target) / 5);
                                target.addVelocity(toFar.x, 0.1, toFar.x);

                                target.setFireTicks(20);
                                if (target instanceof LivingEntity) {
                                    ((LivingEntity) target).addStatusEffect(BlazeStatusEffect.create(10, 0), owner);
                                }
                            }
                        });
            }
            return;
        }
    }

    @Override
    public void event() {
        var owner = getEntity();
        if (owner.world.isClient || 0 < cool || owner.isTouchingWaterOrRain()) {
            return;
        }
        cool = 10;

        //ブレイズ
        if (!blazing) {
            blazing = true;
            cool = 20;
            extinguishTimer = 200;
            owner.world.playSound(null, owner.getX(), owner.getEyeY(), owner.getZ(),
                    SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            blazeBoom(owner, owner);
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, extinguishTimer, 2));
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, extinguishTimer, 1));
            return;
        }

        Vec3d lookFor = owner.getRotationVec(1f);
        Vec3d start = owner.getCameraPosVec(1f);
        Vec3d end = start.add(lookFor.multiply(5));
        var result = ProjectileUtil.raycast(owner, start, end,
                owner.getBoundingBox().expand(5), this::canAttack,
                end.subtract(start).lengthSquared());

        //ブレイズパンチ
        if (result != null) {
            var target = result.getEntity();

            var damageSource = getDamageSource(owner);

            owner.timeUntilRegen = 0;
            if (target.damage(damageSource, 15)) {
                extinguishTimer = 200;

                blazeBoom(owner, target);

                owner.swingHand(Hand.MAIN_HAND, true);

                target.addVelocity(lookFor.x * 0.5, 0.1, lookFor.z * 0.5);

                target.setFireTicks(20);
                if (target instanceof LivingEntity) {
                    ((LivingEntity) target).addStatusEffect(BlazeStatusEffect.create(10, 0), owner);
                }
            }
            return;
        }

        //ブレイズジャンプ
        if (owner.isOnGround() && owner.getPitch() < -15) {
            decreaseExtinguishTimer();
            blazeBoom(owner, owner);

            Vec3d jumpFor = owner.getRotationVec(1f)
                    .multiply(2, 0, 2)
                    .add(0, 1, 0);
            owner.addVelocity(jumpFor.x, jumpFor.y, jumpFor.z);
            owner.velocityModified = true;
            return;
        }

        //ブレイズストンプ
        if (!owner.isOnGround() && owner.isSneaking()) {
            blazeBoom(owner, owner);

            owner.setVelocity(0, -1, 0);
            owner.velocityDirty = true;
            owner.velocityModified = true;
            this.stomp = true;
            return;
        }

    }

    private boolean canAttack(Entity target) {
        var owner = getEntity();
        return !target.isSpectator() && target.isAlive() && target.collides()
                && !owner.isConnectedThroughVehicle(target)
                && (!(target instanceof TameableEntity)
                || !owner.getUuid().equals(((TameableEntity) target).getOwnerUuid()));
    }

    private void blazeBoom(LivingEntity owner, Entity target) {
        target.world.playSound(null, target.getX(), target.getEyeY(), target.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.0f, 2.0f);
        ((ServerWorld) target.world).spawnParticles(ParticleTypes.FLAME,
                target.getX(),
                target.getY() + target.getHeight() / 2,
                target.getZ(),
                100, 0, 1, 0, 0.25);

        var rand = owner.getRandom();

        for (int i = 0; i < 5; i++) {
            var blazeFire = new BlazeFireEntity(target.world, owner);
            blazeFire.setPosition(target.getX(), target.getY() + 0.5, target.getZ());
            var vec = target.getVelocity();
            blazeFire.setVelocity(
                    (rand.nextFloat() * 2 - 1) * 0.5f + vec.x,
                    rand.nextFloat() * 0.5f + vec.y,
                    (rand.nextFloat() * 2 - 1) * 0.5f + vec.z);
            target.world.spawnEntity(blazeFire);
        }
    }

    private void decreaseExtinguishTimer() {
        extinguishTimer = Math.min(extinguishTimer - 20, 200);
    }

    private DamageSource getDamageSource(LivingEntity owner) {
        DamageSource damageSource;
        if (owner instanceof PlayerEntity player) {
            damageSource = DamageSource.player(player);
        } else {
            damageSource = DamageSource.mob(owner);
        }
        damageSource = damageSource.setExplosive();
        damageSource = ((DamageSourceAccessor) damageSource).setFireMethod();
        return damageSource;
    }

}
