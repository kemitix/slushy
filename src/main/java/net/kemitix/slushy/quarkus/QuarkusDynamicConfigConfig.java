package net.kemitix.slushy.quarkus;

import lombok.Getter;
import net.kemitix.slushy.app.config.DynamicConfigConfig;

import javax.enterprise.context.ApplicationScoped;

@Getter
@ApplicationScoped
public class QuarkusDynamicConfigConfig
        implements DynamicConfigConfig {

    private final String listName = System.getenv("SLUSHY_CONFIG_LIST");
    private final String cardName = System.getenv("SLUSHY_CONFIG_CARD");

}
