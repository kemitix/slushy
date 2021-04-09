package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.DueDays;
import net.kemitix.slushy.app.ListProcessConfig;
import net.kemitix.slushy.app.config.DynamicConfigConfig;

import javax.inject.Inject;

public interface InboxConfig
        extends ListProcessConfig, DueDays {

}
