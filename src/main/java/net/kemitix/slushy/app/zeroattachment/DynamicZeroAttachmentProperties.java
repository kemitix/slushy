package net.kemitix.slushy.app.zeroattachment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.kemitix.slushy.app.config.DynamicConfig;

@ApplicationScoped
public class DynamicZeroAttachmentProperties
        implements ZeroAttachmentProperties, DynamicConfig {

    @Inject
    ZeroAttachmentConfigMapping configMapping;

    @Override
    public String routingSlip() {
        return findValue(ZeroAttachmentConfigMapping.PREFIX, ROUTING_SLIP)
                .orElseGet(configMapping::routingSlip);
    }

}
