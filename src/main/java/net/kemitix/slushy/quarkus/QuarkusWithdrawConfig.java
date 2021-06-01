package net.kemitix.slushy.quarkus;

import io.quarkus.arc.config.ConfigProperties;
import net.kemitix.slushy.app.withdraw.WithdrawConfig;

@ConfigProperties(prefix = QuarkusWithdrawConfig.CONFIG_PREFIX)
public class QuarkusWithdrawConfig
        extends AbstractQuarkusListProcessingConfig
        implements WithdrawConfig {

    protected static final String CONFIG_PREFIX = "slushy.withdraw";

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

}
