package net.abilities.ability;

import com.google.common.collect.Maps;
import net.minecraft.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbilityManager {
    public static final AbilityManager INSTANCE = new AbilityManager();
    private static final Map<String, AbilityFactory> factoryMap = Maps.newHashMap();

    public void register(String name, AbilityFactory factory) {
        factoryMap.put(name, factory);
    }

    public List<Ability> create(LivingEntity entity) {
        return factoryMap.entrySet().stream().map(e -> e.getValue().create(e.getKey(), entity)).collect(Collectors.toList());
    }

    public Optional<Ability> create(String name, LivingEntity entity) {
        return factoryMap.containsKey(name)
                ? Optional.of(factoryMap.get(name).create(name, entity))
                : Optional.empty();
    }

    public List<String> getAbilityNames() {
        return Lists.newArrayList(factoryMap.keySet().iterator());
    }

    public interface AbilityFactory {
        Ability create(String name, LivingEntity entity);
    }

}
