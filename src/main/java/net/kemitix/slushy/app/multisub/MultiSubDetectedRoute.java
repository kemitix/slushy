package net.kemitix.slushy.app.multisub;

import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MultiSubDetectedRoute
        extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:Slushy.MultiSubDetected")
                .routeId("Slushy.MultiSubDetected")
                .log("Submission rejected due to an existing submission")
                .setHeader("SlushyRejection").body()
                .setHeader("SlushyEmailTemplate").constant("multisub")
                .to("direct:Slushy.SendEmail")

                // move card to rejected
                .to("direct:Slushy.Reject.MoveToTargetList")
        ;
    }
}
