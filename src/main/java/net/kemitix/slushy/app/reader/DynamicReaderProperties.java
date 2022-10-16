package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.DynamicListProcessConfig;
import net.kemitix.slushy.app.config.DynamicConfig;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.kemitix.trello.TrelloConfig;

@ApplicationScoped
public class DynamicReaderProperties
        implements ReaderProperties, DynamicConfig {

    @Inject
    ReaderConfigMapping configMapping;
    @Inject
    TrelloConfig trelloConfig;

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

    @Override
    public String reader() {
        return findValue(ReaderConfigMapping.PREFIX, READER)
                .filter(reader -> !"environment".equals(reader))
                .orElseGet(trelloConfig::getReader);
    }
}
