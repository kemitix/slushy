package net.kemitix.slushy.app.email;

import net.kemitix.slushy.app.config.DynamicConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DynamicEmailProperties
        implements EmailProperties, DynamicConfig {

    @Inject EmailConfigMapping configMapping;

    @Override
    public long scanPeriod() {
        return findLongValue(EmailConfigMapping.PREFIX, SCAN_PERIOD)
                .orElseGet(configMapping::scanPeriod);
    }

    @Override
    public long retryDelay() {
        return findLongValue(EmailConfigMapping.PREFIX, RETRY_DELAY)
                .orElseGet(configMapping::retryDelay);
    }

}
