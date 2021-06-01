package net.kemitix.slushy.quarkus;

import io.quarkus.arc.config.ConfigProperties;
import net.kemitix.slushy.app.archiver.ArchiverConfig;

@ConfigProperties(prefix = QuarkusArchiverConfig.CONFIG_PREFIX)
public class QuarkusArchiverConfig
        extends AbstractQuarkusListProcessingConfig
        implements ArchiverConfig {

    protected static final String CONFIG_PREFIX = "slushy.archiver";

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

}
