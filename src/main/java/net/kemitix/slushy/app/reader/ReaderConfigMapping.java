package net.kemitix.slushy.app.reader;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = ReaderConfigMapping.PREFIX)
public interface ReaderConfigMapping
        extends ReaderProperties {
    String PREFIX = "slushy.reader";
}
