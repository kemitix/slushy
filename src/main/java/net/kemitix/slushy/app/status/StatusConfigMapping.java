package net.kemitix.slushy.app.status;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = StatusConfigMapping.PREFIX)
public interface StatusConfigMapping
        extends StatusProperties {
    String PREFIX = "slushy.stats";
}
