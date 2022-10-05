package net.kemitix.slushy.app.fileconversion;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = CalibraConverterConfigMapping.PREFIX)
public interface CalibraConverterConfigMapping
        extends CalibreConverterProperties {
    String PREFIX = "slushy.converter.calibre";
}
