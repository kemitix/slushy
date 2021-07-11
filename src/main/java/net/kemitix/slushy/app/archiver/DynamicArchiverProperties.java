package net.kemitix.slushy.app.archiver;

import net.kemitix.slushy.app.DynamicListProcessConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DynamicArchiverProperties
        implements ArchiverProperties {

    @Inject ArchiverConfigMapping configMapping;

    DynamicListProcessConfig config;

    @PostConstruct
    void init() {
        config = new DynamicListProcessConfig(ArchiverConfigMapping.PREFIX, configMapping);
    }

    @Override
    public String sourceList() {
        return config.sourceList();
    }

    @Override
    public String targetList() {
        return config.targetList();
    }

    @Override
    public String routingSlip() {
        return config.routingSlip();
    }

    @Override
    public int requiredAgeHours() {
        return config.requiredAgeHours();
    }

    @Override
    public long scanPeriod() {
        return config.scanPeriod();
    }

    @Override
    public long retryDelay() {
        return config.retryDelay();
    }

}
