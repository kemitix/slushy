package net.kemitix.slushy.quarkus;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Setter;
import net.kemitix.slushy.app.reader.ReaderConfig;

@Setter
@ConfigProperties(prefix = QuarkusReaderConfig.CONFIG_PREFIX)
public class QuarkusReaderConfig
        extends AbstractQuarkusListProcessingConfig
        implements ReaderConfig {

    protected static final String CONFIG_PREFIX = "slushy.reader";

    private int maxSize;

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

    @Override
    public int getMaxSize() {
        return findIntegerValue("max-size")
                .orElse(maxSize);
    }

}
