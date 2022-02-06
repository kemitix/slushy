package net.kemitix.slushy.app.inbox;

import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VerifyAttachmentValidTypeRoute
        extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:Slushy.VerifyAttachmentValidType")
                .routeId("Slushy.VerifyAttachmentValidType")
                .choice()
                .when(simple("${body.hasDocument}"))
                .otherwise()
                .to("direct:Slushy.InvalidAttachment")
                .process(exchange -> exchange.setRouteStop(true))
                .end()
        ;
    }
}
