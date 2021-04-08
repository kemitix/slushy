package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.config.AbstractDynamicConfig;
import net.kemitix.slushy.app.zeroattachment.ZeroAttachmentConfig;

import java.util.Properties;

@Setter
@Getter
@ConfigProperties(prefix = QuarkusZeroAttachmentConfig.CONFIG_PREFIX)
public class QuarkusZeroAttachmentConfig
        extends AbstractDynamicConfig
        implements ZeroAttachmentConfig {

    protected static final String CONFIG_PREFIX = "slushy.zeroattachment";

    String routingSlip;

    @Override
    public void update(Properties properties) {
        super.update(properties);
        update("routing-slip", this::setRoutingSlip, properties);
    }

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }
}
