package net.kemitix.slushy.app;

import net.kemitix.slushy.app.config.DynamicConfig;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class DynamicListProcessConfig
        implements ListProcessProperties, DynamicConfig {

    private final String prefix;
    private final ListProcessProperties properties;

    public DynamicListProcessConfig(
            String prefix,
            ListProcessProperties properties
    ) {
        this.prefix = prefix;
        this.properties = properties;
    }

    @Override
    public String sourceList() {
        return findValue(prefix, SOURCE_LIST)
                .orElseGet(properties::sourceList);
    }

    @Override
    public String targetList() {
        return findValue(prefix, TARGET_LIST)
                .orElseGet(properties::targetList);
    }

    @Override
    public String routingSlip() {
        return findValue(prefix, ROUTING_SLIP)
                .orElseGet(properties::routingSlip);
    }

    @Override
    public int requiredAgeHours() {
        return findIntegerValue(prefix, REQUIRED_AGE_HOURS)
                .orElseGet(properties::requiredAgeHours);
    }

    @Override
    public long scanPeriod() {
        return findLongValue(prefix, SCAN_PERIOD)
                .orElseGet(properties::scanPeriod);
    }

    @Override
    public long retryDelay() {
        return findLongValue(prefix, RETRY_DELAY)
                .orElseGet(properties::retryDelay);
    }

}
