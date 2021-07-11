package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.DynamicListProcessConfig;
import net.kemitix.slushy.app.config.DynamicConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DynamicReaderProperties
        implements ReaderProperties, DynamicConfig {

    @Inject
    ReaderConfigMapping configMapping;

    DynamicListProcessConfig config;

    @PostConstruct
    void init() {
        config = new DynamicListProcessConfig(ReaderConfigMapping.PREFIX, configMapping);
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

    @Override
    public int maxSize() {
        return findIntegerValue(ReaderConfigMapping.PREFIX, MAX_SIZE)
                .orElseGet(configMapping::maxSize);
    }

}
