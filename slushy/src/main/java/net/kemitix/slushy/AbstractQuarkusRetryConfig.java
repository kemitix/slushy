package net.kemitix.slushy;

import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.RetryConfig;

@Setter
@Getter
public abstract class AbstractQuarkusRetryConfig
        implements RetryConfig {

    long scanPeriod;
    long retryDelay;

}
