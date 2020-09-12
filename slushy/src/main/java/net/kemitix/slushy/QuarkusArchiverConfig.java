package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import net.kemitix.slushy.app.archiver.ArchiverConfig;

@ConfigProperties(prefix = "slushy.archiver")
public class QuarkusArchiverConfig
        extends AbstractQuarkusListProcessingConfig
        implements ArchiverConfig {

}
