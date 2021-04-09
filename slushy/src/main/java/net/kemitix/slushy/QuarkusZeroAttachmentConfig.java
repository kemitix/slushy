package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Setter;
import net.kemitix.slushy.app.config.AbstractDynamicConfig;
import net.kemitix.slushy.app.zeroattachment.ZeroAttachmentConfig;

@Setter
@ConfigProperties(prefix = QuarkusZeroAttachmentConfig.CONFIG_PREFIX)
public class QuarkusZeroAttachmentConfig
        extends AbstractDynamicConfig
        implements ZeroAttachmentConfig {

    protected static final String CONFIG_PREFIX = "slushy.zeroattachment";

    String routingSlip;

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

    public String getRoutingSlip() {
        return findValue("routing-slip")
                .orElse(routingSlip);
    }

}
