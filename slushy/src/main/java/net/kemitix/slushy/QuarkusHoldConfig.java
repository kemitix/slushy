package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Setter;
import net.kemitix.slushy.app.hold.HoldConfig;

@Setter
@ConfigProperties(prefix = QuarkusHoldConfig.CONFIG_PREFIX)
public class QuarkusHoldConfig
        extends AbstractQuarkusListProcessingConfig
        implements HoldConfig {

    protected static final String CONFIG_PREFIX = "slushy.hold";

    private long dueDays;

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

    @Override
    public long getDueDays() {
        return findLongValue("due-days")
                .orElse(dueDays);
    }

}
