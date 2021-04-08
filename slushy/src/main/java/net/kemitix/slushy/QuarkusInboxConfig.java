package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.inbox.InboxConfig;
import org.apache.velocity.runtime.directive.Parse;

import java.util.Properties;

@Setter
@Getter
@ConfigProperties(prefix = QuarkusInboxConfig.CONFIG_PREFIX)
public class QuarkusInboxConfig
        extends AbstractQuarkusListProcessingConfig
        implements InboxConfig {

    protected static final String CONFIG_PREFIX = "slushy.inbox";

    private Long dueDays;

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

    @Override
    public void update(Properties properties) {
        update("due-days", Long::parseLong, this::setDueDays, properties);
    }
}
