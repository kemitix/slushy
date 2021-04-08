package net.kemitix.slushy.app.config;

import lombok.extern.java.Log;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

@Log
public abstract class AbstractDynamicConfig
        implements DynamicConfig {

    @Override
    public void update(Properties properties) {
        // do nothing
    }

    protected <T> void update(
            String key,
            Function<String, T> parser,
            Consumer<T> setter,
            Properties properties
    ) {
        String property = getConfigPrefix() + "." + key;
        if (properties.containsKey(property)) {
            T value = parser.apply((String) properties.get(property));
            log.info(String.format("Update %s to %s", property, value));
            setter.accept(value);
        }
    }

    protected void update(
            String key,
            Consumer<String> setter,
            Properties properties
    ) {
        update(key, Function.identity(), setter, properties);
    }


}
