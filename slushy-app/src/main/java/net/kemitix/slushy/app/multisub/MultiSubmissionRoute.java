package net.kemitix.slushy.app.multisub;

import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.email.SendEmail;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MultiSubmissionRoute
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject IsMultipleSubmission isMultipleSubmission;

    @Override
    public void configure() {
        from("direct:Slushy.MultiSubMonitor")
                .routeId("Slushy.MultiSubMonitor")
                .bean(isMultipleSubmission)
                .choice()
                .when(body().isNotNull())
                .to("direct:Slushy.MultiSubDetected")
                .process(exchange -> exchange.setRouteStop(true))
                .end()
        ;

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
