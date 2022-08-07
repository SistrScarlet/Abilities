package net.abilities.ability;

public class Abilities {

    public static void init() {
        register("fly", FlyAbility::new);
        register("leap", LeapAbility::new);
        register("dodge", DodgeAbility::new);
        register("blaze", BlazeAbility::new);
    }

    public static void register(String name, AbilityManager.AbilityFactory factory) {
        AbilityManager.INSTANCE.register(name, factory);
    }

}
