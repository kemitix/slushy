package net.kemitix.slushy.app.email;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.kemitix.slushy.app.config.DynamicConfig;

@ApplicationScoped
public class DynamicEmailProperties
        implements EmailProperties, DynamicConfig {

    @Inject
    EmailConfigMapping configMapping;

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
