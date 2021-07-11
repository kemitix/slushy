package net.kemitix.slushy.app.config;

import lombok.Getter;

import java.util.Properties;

public class DynamicProperties {

    public static final DynamicProperties INSTANCE = new DynamicProperties();

    @Getter
    private final Properties properties = new Properties();

}
