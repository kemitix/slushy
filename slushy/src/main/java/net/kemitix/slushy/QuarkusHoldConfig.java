package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.hold.HoldConfig;

import java.util.Properties;

@Setter
@Getter
@ConfigProperties(prefix = QuarkusHoldConfig.CONFIG_PREFIX)
public class QuarkusHoldConfig
        extends AbstractQuarkusListProcessingConfig
        implements HoldConfig {

    protected static final String CONFIG_PREFIX = "slushy.hold";

    private Long dueDays;

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

    @Override
    public void update(Properties properties) {
        update("due-days", Long::parseLong, v -> {this.dueDays = v;}, properties);
    }

}
