package net.kemitix.slushy.app.zeroattachment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;

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
