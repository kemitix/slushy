package net.kemitix.slushy.app.multisub;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = MultiSubConfigMapping.PREFIX)
public interface MultiSubConfigMapping
        extends MultiSubProperties {
    String PREFIX = "slushy.multisub";
}
