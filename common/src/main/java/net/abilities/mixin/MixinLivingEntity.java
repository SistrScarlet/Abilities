package net.abilities.mixin;

import com.google.common.collect.Maps;
import net.abilities.HasAbilities;
import net.abilities.ability.Ability;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(LivingEntity.class)
public class MixinLivingEntity implements HasAbilities {
    private final Map<String, Ability> abilityMap = Maps.newHashMap();

    @Override
    public Optional<Ability> getAbility_abilities(String name) {
        return Optional.ofNullable(this.abilityMap.get(name));
    }

    @Override
    public void addAbility(Ability ability) {
        this.abilityMap.put(ability.getName(), ability);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        abilityMap.values().forEach(Ability::tick);
    }
}
