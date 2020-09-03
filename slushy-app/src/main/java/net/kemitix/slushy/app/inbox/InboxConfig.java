package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.ListProcessConfig;

public interface InboxConfig
        extends ListProcessConfig {

    /**
     * How long before a still-waiting-to-be-read response should be sent.
     */
    long getDueDays();

}
