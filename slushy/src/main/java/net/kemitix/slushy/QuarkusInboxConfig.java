package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Setter;
import net.kemitix.slushy.app.inbox.InboxConfig;

@Setter
@ConfigProperties(prefix = QuarkusInboxConfig.CONFIG_PREFIX)
public class QuarkusInboxConfig
        extends AbstractQuarkusListProcessingConfig
        implements InboxConfig {

    protected static final String CONFIG_PREFIX = "slushy.inbox";

    private long dueDays;

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

    @Override
    public long getDueDays() {
        return findValue("due-days")
                .map(Long::parseLong)
                .orElse(dueDays);
    }
}
