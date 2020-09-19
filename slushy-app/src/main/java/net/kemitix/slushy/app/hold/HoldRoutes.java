package net.kemitix.slushy.app.hold;

import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.OnException;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.TemplatedRouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static net.kemitix.slushy.app.ListProcessRouteTemplate.PARAM_LIST_NAME;
import static net.kemitix.slushy.app.ListProcessRouteTemplate.PARAM_NAME;
import static net.kemitix.slushy.app.ListProcessRouteTemplate.PARAM_REQUIRED_AGE_HOURS;
import static net.kemitix.slushy.app.ListProcessRouteTemplate.PARAM_ROUTING_SLIP;
import static net.kemitix.slushy.app.ListProcessRouteTemplate.PARAM_SCAN_PERIOD;
import static net.kemitix.slushy.app.ListProcessRouteTemplate.ROUTE_LIST_PROCESS;

@ApplicationScoped
public class HoldRoutes
        extends RouteBuilder {

    @Inject HoldConfig holdConfig;
    @Inject MoveCard moveCard;
    @Inject CamelContext camelContext;

    @Override
    public void configure() {
        OnException.retry(this, holdConfig);

        TemplatedRouteBuilder.builder(camelContext, ROUTE_LIST_PROCESS)
                .parameter(PARAM_NAME, "hold")
                .parameter(PARAM_SCAN_PERIOD, holdConfig.getScanPeriod())
                .parameter(PARAM_LIST_NAME, holdConfig.getSourceList())
                .parameter(PARAM_REQUIRED_AGE_HOURS, holdConfig.getRequiredAgeHours())
                .parameter(PARAM_ROUTING_SLIP, holdConfig.getRoutingSlip())
                .add()
        ;

        from("direct:Slushy.Hold.SendEmail")
                .routeId("Slushy.Hold.SendEmail")
                .setHeader("SlushyEmailTemplate").constant("hold")
                .to("direct:Slushy.SendEmail")
        ;

        from("direct:Slushy.Hold.MoveToTargetList")
                .routeId("Slushy.Hold.MoveToTargetList")
                .setHeader("SlushyTargetList", holdConfig::getTargetList)
                .bean(moveCard)
        ;

    }

}
