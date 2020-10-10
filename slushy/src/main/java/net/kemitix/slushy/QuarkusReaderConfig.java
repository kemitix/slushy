package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.reader.ReaderConfig;

@Setter
@Getter
@ConfigProperties(prefix = "slushy.reader")
public class QuarkusReaderConfig
        extends AbstractQuarkusListProcessingConfig
        implements ReaderConfig {

    private int maxSize;

}
