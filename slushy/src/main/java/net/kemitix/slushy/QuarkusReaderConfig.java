package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import net.kemitix.slushy.app.reader.ReaderConfig;

@ConfigProperties(prefix = "slushy.reader")
public class QuarkusReaderConfig
        extends AbstractQuarkusListProcessingConfig
        implements ReaderConfig {

}
