package net.abilities.entity.effect;

import net.abilities.mixin.DamageSourceAccessor;
import net.abilities.setup.Registration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;

public class BlazeStatusEffect extends StatusEffect {
    public BlazeStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public static StatusEffectInstance create(int duration, int amp) {
        return new StatusEffectInstance(Registration.BLAZE.get(), duration, amp,
                false, false, true);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.timeUntilRegen = 0;
        entity.damage(DamageSource.IN_FIRE, amplifier + 1);

        //火炎耐性無視
        var damageSource = DamageSource.IN_FIRE;
        ((DamageSourceAccessor) damageSource).setFireField(false);
        entity.timeUntilRegen = 0;
        entity.damage(damageSource, amplifier + 1);

        var rand = entity.getRandom();

        var vec = entity.getVelocity().multiply(0.9);

        for (int i = 0; i < 3; i++) {
            entity.world.addParticle(ParticleTypes.FLAME,
                    entity.getParticleX(1.1),
                    (entity.getY() + entity.getHeight() / 2) + entity.getHeight() * (rand.nextFloat() * 2 - 1) * 1.1,
                    entity.getParticleZ(1.1),
                    (rand.nextFloat() * 2 - 1) + vec.x,
                    rand.nextFloat() + vec.y,
                    (rand.nextFloat() * 2 - 1) + vec.z);
        }

    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % (amplifier + 2) != 0;
    }

}
