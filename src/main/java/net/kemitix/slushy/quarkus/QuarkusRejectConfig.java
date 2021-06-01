package net.kemitix.slushy.quarkus;

import io.quarkus.arc.config.ConfigProperties;
import net.kemitix.slushy.app.reject.RejectConfig;

@ConfigProperties(prefix = QuarkusRejectConfig.CONFIG_PREFIX)
public class QuarkusRejectConfig
        extends AbstractQuarkusListProcessingConfig
        implements RejectConfig {

    protected static final String CONFIG_PREFIX = "slushy.reject";

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

}
