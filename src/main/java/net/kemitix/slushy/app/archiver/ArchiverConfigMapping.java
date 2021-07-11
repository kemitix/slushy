package net.kemitix.slushy.app.archiver;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = ArchiverConfigMapping.PREFIX)
public interface ArchiverConfigMapping
        extends ArchiverProperties {
    String PREFIX = "slushy.archiver";
}
