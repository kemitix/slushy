package net.kemitix.slushy.app.hold;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = HoldConfigMapping.PREFIX)
public interface HoldConfigMapping
        extends HoldProperties {
    String PREFIX = "slushy.hold";
}
