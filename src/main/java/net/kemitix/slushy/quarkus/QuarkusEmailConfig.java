package net.kemitix.slushy.quarkus;

import io.quarkus.arc.config.ConfigProperties;
import net.kemitix.slushy.app.email.EmailConfig;

@ConfigProperties(prefix = QuarkusEmailConfig.CONFIG_PREFIX)
public class QuarkusEmailConfig
        extends AbstractQuarkusRetryConfig
        implements EmailConfig {

    protected static final String CONFIG_PREFIX = "slushy.email";

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

}
