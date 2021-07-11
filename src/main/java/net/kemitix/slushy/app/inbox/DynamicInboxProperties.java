package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.DynamicListProcessConfig;
import net.kemitix.slushy.app.config.DynamicConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DynamicInboxProperties
        implements InboxProperties, DynamicConfig {

    @Inject
    InboxConfigMapping configMapping;

    DynamicListProcessConfig config;

    @PostConstruct
    void init() {
        config = new DynamicListProcessConfig(InboxConfigMapping.PREFIX, configMapping);
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
    public long dueDays() {
        return findLongValue(InboxConfigMapping.PREFIX, DUE_DAYS)
                .orElseGet(configMapping::dueDays);
    }
}
