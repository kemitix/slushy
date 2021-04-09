package net.kemitix.slushy;

import lombok.Setter;
import net.kemitix.slushy.app.RetryConfig;
import net.kemitix.slushy.app.config.AbstractDynamicConfig;

@Setter
public abstract class AbstractQuarkusRetryConfig
        extends AbstractDynamicConfig
        implements RetryConfig {

    private long scanPeriod;
    private long retryDelay;

    @Override
    public long getScanPeriod() {
        return findLongValue("scan-period")
                .orElse(scanPeriod);
    }

    @Override
    public long getRetryDelay() {
        return findLongValue("retry-delay")
                .orElse(retryDelay);
    }
}
