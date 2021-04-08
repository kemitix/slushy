package net.kemitix.slushy;

import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.config.AbstractDynamicConfig;
import net.kemitix.slushy.app.ListProcessConfig;

@Setter
@Getter
public abstract class AbstractQuarkusListProcessingConfig
        extends AbstractDynamicConfig
        implements ListProcessConfig {

    long scanPeriod;
    String sourceList;
    String targetList;
    String routingSlip;
    int requiredAgeHours;
    long retryDelay;

}
