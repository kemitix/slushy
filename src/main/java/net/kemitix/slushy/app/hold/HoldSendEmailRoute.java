package net.kemitix.slushy.app.hold;

import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HoldSendEmailRoute
        extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:Slushy.Hold.SendEmail")
                .routeId("Slushy.Hold.SendEmail")
                .setHeader("SlushyEmailTemplate").constant("hold")
                .to("direct:Slushy.SendEmail")
        ;
    }
}
