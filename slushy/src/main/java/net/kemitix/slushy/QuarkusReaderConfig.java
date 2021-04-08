package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.reader.ReaderConfig;

import java.util.Properties;

@Setter
@Getter
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
    public void update(Properties properties) {
        update("max-size", Integer::parseInt, this::setMaxSize, properties);
    }
}
