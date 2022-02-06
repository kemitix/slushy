package net.kemitix.slushy.app.reject;

import net.kemitix.slushy.app.DynamicListProcessConfig;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DynamicRejectProperties
        implements RejectProperties {

    @Inject
    RejectConfigMapping configMapping;

    DynamicListProcessConfig config;

    @PostConstruct
    void init() {
        config = new DynamicListProcessConfig(RejectConfigMapping.PREFIX, configMapping);
    }

    @Override
    public String sourceList() {
        return configMapping.sourceList();
    }

    @Override
    public String targetList() {
        return configMapping.targetList();
    }

    @Override
    public String routingSlip() {
        return configMapping.routingSlip();
    }

    @Override
    public int requiredAgeHours() {
        return configMapping.requiredAgeHours();
    }

    @Override
    public long scanPeriod() {
        return configMapping.scanPeriod();
    }

    @Override
    public long retryDelay() {
        return configMapping.retryDelay();
    }

}
