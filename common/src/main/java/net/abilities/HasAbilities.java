package net.abilities;

import net.abilities.ability.Ability;

import java.util.Optional;

public interface HasAbilities {

    Optional<Ability> getAbility_abilities(String name);

    void addAbility(Ability ability);

}
