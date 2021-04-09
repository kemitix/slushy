package net.kemitix.slushy.app.config;

import java.util.Optional;
import java.util.Properties;

public abstract class AbstractDynamicConfig
        implements DynamicConfig {

    protected Optional<String> findValue(String name) {
        String key = getConfigPrefix() + "." + name;
        Properties properties = DynamicProperties.INSTANCE.getProperties();
        if (properties.containsKey(key)) {
            return Optional.ofNullable(properties.getProperty(key));
        }
        return Optional.empty();
    }

    protected Optional<Long> findLongValue(String name) {
        return findValue(name)
                .map(Long::parseLong);
    }

    protected Optional<Integer> findIntegerValue(String name) {
        return findValue(name)
                .map(Integer::parseInt);
    }

}
