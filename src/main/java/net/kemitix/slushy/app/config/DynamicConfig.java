package net.kemitix.slushy.app.config;

import java.util.Optional;
import java.util.Properties;

public interface DynamicConfig {

    default Optional<String> findValue(String prefix, String name) {
        String key = prefix + "." + name;
        Properties properties = DynamicProperties.INSTANCE.getProperties();
        if (properties.containsKey(key)) {
            return Optional.ofNullable(properties.getProperty(key));
        }
        return Optional.empty();
    }

    default Optional<Long> findLongValue(String prefix, String name) {
        return findValue(prefix, name)
                .map(Long::parseLong);
    }

    default Optional<Integer> findIntegerValue(String prefix, String name) {
        return findValue(prefix, name)
                .map(Integer::parseInt);
    }

}
