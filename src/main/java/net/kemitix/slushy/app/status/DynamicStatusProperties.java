package net.kemitix.slushy.app.status;

import net.kemitix.slushy.app.config.DynamicConfig;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DynamicStatusProperties
        implements StatusProperties, DynamicConfig {

    @Inject StatusConfigMapping configMapping;

    @Override
    public int logPeriod() {
        return findIntegerValue(StatusConfigMapping.PREFIX, LOG_PERIOD)
                .orElseGet(configMapping::logPeriod);
    }

}
