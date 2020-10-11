package net.kemitix.slushy.app.multisub;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MultiSubMonitorRoute
        extends RouteBuilder {

    private final IsMultipleSubmission isMultipleSubmission;

    @Inject
    public MultiSubMonitorRoute(IsMultipleSubmission isMultipleSubmission) {
        this.isMultipleSubmission = isMultipleSubmission;
    }

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
    }

}
