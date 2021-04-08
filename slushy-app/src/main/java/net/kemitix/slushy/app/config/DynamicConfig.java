package net.kemitix.slushy.app.config;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

public interface DynamicConfig {

    String getConfigPrefix();

    void update(Properties properties);

}
