package net.kemitix.slushy.app.reject;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = RejectConfigMapping.PREFIX)
public interface RejectConfigMapping
        extends RejectProperties {
    String PREFIX = "slushy.reject";
}
