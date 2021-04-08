package net.kemitix.slushy;

import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.ListProcessConfig;

import java.util.Properties;

@Setter
@Getter
public abstract class AbstractQuarkusListProcessingConfig
        extends AbstractQuarkusRetryConfig
        implements ListProcessConfig {

    private String sourceList;
    private String targetList;
    private String routingSlip;
    private int requiredAgeHours;

    @Override
    public void update(Properties properties) {
        super.update(properties);
        update("source-list", this::setSourceList, properties);
        update("target-list", this::setTargetList, properties);
        update("routing-slip", this::setRoutingSlip, properties);
        update("required-age-hours", Integer::parseInt, this::setRequiredAgeHours, properties);
    }
}
