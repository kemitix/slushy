package net.kemitix.slushy;

import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.RetryConfig;
import net.kemitix.slushy.app.config.AbstractDynamicConfig;

import java.util.Properties;

@Setter
@Getter
public abstract class AbstractQuarkusRetryConfig
        extends AbstractDynamicConfig
        implements RetryConfig {

    private long scanPeriod;
    private long retryDelay;

    @Override
    public void update(Properties properties) {
        super.update(properties);
        //Not dynamically updatable
        //update("scan-period", Long::parseLong, this::setScanPeriod, properties);
        update("retry-delay", Long::parseLong, this::setRetryDelay, properties);
    }

}
