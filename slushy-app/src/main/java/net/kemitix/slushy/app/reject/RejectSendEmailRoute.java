package net.kemitix.slushy.app.reject;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RejectSendEmailRoute
        extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:Slushy.Reject.SendEmail")
                .routeId("Slushy.Reject.SendEmail")
                .setHeader("SlushyEmailTemplate").constant("reject")
                .to("direct:Slushy.SendEmail")
        ;
    }
}
