package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.config.AbstractDynamicConfig;
import net.kemitix.slushy.app.status.StatusConfig;

import java.util.Properties;

@Setter
@Getter
@ConfigProperties(prefix = QuarkusStatusConfig.CONFIG_PREFIX)
public class QuarkusStatusConfig
        extends AbstractDynamicConfig
        implements StatusConfig {

    protected static final String CONFIG_PREFIX = "slushy.status";

    private int logPeriod;
    private String listName = System.getenv("SLUSHY_STATUS_LIST");
    private String cardName = System.getenv("SLUSHY_STATUS_CARD");

    @Override
    public void update(Properties properties) {
        super.update(properties);
        update("log-period", Integer::parseInt, this::setLogPeriod, properties);
    }

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }
}
