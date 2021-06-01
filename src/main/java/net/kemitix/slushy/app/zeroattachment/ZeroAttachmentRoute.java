package net.kemitix.slushy.app.zeroattachment;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ZeroAttachmentRoute
        extends RouteBuilder {

    private final ZeroAttachmentConfig zeroAttachmentConfig;

    @Inject
    public ZeroAttachmentRoute(ZeroAttachmentConfig zeroAttachmentConfig) {
        this.zeroAttachmentConfig = zeroAttachmentConfig;
    }

    @Override
    public void configure() {
        from("direct:Slushy.ZeroAttachment")
                .routeId("Slushy.ZeroAttachment")
                .setHeader("SlushyRoutingSlip", zeroAttachmentConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
