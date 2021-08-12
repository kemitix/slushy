package net.kemitix.slushy.app.zeroattachment;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ZeroAttachmentRoute
        extends RouteBuilder {

    private final ZeroAttachmentProperties zeroAttachmentProperties;

    @Inject
    public ZeroAttachmentRoute(
            DynamicZeroAttachmentProperties zeroAttachmentProperties
    ) {
        this.zeroAttachmentProperties = zeroAttachmentProperties;
    }

    @Override
    public void configure() {
        from("direct:Slushy.ZeroAttachment")
                .routeId("Slushy.ZeroAttachment")
                .setHeader("SlushyRoutingSlip", zeroAttachmentProperties::routingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
