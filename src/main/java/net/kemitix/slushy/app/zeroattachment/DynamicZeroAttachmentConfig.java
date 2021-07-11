package net.kemitix.slushy.app.zeroattachment;

import net.kemitix.slushy.app.config.DynamicConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DynamicZeroAttachmentConfig
        implements ZeroAttachmentProperties, DynamicConfig {

    @Inject
    ZeroAttachmentConfigMapping configMapping;

    @Override
    public String routingSlip() {
        return findValue(ZeroAttachmentConfigMapping.PREFIX, ROUTING_SLIP)
                .orElseGet(configMapping::routingSlip);
    }

}
