package net.kemitix.slushy.app.multisub;

import net.kemitix.slushy.app.config.DynamicConfig;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DynamicMultiSubProperties
        implements MultiSubProperties, DynamicConfig {

    @Inject
    MultiSubConfigMapping configMapping;

    @Override
    public String lists() {
        return findValue(MultiSubConfigMapping.PREFIX, LISTS)
                .orElseGet(configMapping::lists);
    }

}
