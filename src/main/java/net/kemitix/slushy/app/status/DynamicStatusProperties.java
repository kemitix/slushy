package net.kemitix.slushy.app.status;

import net.kemitix.slushy.app.config.DynamicConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
