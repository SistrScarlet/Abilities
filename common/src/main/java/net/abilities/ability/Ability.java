package net.abilities.ability;

import net.minecraft.entity.LivingEntity;

public interface Ability {

    String getName();

    void tick();

    void event();

    abstract class Base implements Ability {
        private final String name;
        private final LivingEntity entity;

        public Base(String name, LivingEntity entity) {
            this.name = name;
            this.entity = entity;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public LivingEntity getEntity() {
            return entity;
        }
    }

}
