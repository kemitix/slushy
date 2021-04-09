package net.kemitix.slushy.app.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

@Setter
@Getter
public class DynamicProperties {

    public static final DynamicProperties INSTANCE = new DynamicProperties();

    private final Properties properties = new Properties();

}
