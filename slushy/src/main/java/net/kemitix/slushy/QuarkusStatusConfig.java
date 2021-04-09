package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.config.AbstractDynamicConfig;
import net.kemitix.slushy.app.status.StatusConfig;

@Setter
@ConfigProperties(prefix = QuarkusStatusConfig.CONFIG_PREFIX)
public class QuarkusStatusConfig
        extends AbstractDynamicConfig
        implements StatusConfig {

    protected static final String CONFIG_PREFIX = "slushy.status";

    private int logPeriod;
    @Getter
    private String listName = System.getenv("SLUSHY_STATUS_LIST");
    @Getter
    private String cardName = System.getenv("SLUSHY_STATUS_CARD");

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

    @Override
    public int getLogPeriod() {
        return findIntegerValue("log-period")
                .orElse(logPeriod);
    }
}
