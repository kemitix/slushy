package net.kemitix.slushy.app.status;

import net.kemitix.slushy.app.CardIdentifierConfig;
import net.kemitix.slushy.app.ListIdentifierConfig;

public interface StatusConfig
        extends ListIdentifierConfig, CardIdentifierConfig {

    /**
     * How often to report the status in seconds.
     */
    int getLogPeriod();

}
