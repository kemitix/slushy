package net.kemitix.slushy.app.reject;

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
public class RejectRoutes
        extends RouteBuilder {

    @Inject RejectConfig rejectConfig;
    @Inject MoveCard moveCard;
    @Inject CamelContext camelContext;

    @Override
    public void configure() {
        OnException.retry(this, rejectConfig);

        TemplatedRouteBuilder.builder(camelContext, ROUTE_LIST_PROCESS)
                .parameter(PARAM_NAME, "reject")
                .parameter(PARAM_SCAN_PERIOD, rejectConfig.getScanPeriod())
                .parameter(PARAM_LIST_NAME, rejectConfig.getSourceList())
                .parameter(PARAM_REQUIRED_AGE_HOURS, rejectConfig.getRequiredAgeHours())
                .parameter(PARAM_ROUTING_SLIP, rejectConfig.getRoutingSlip())
                .add()
        ;

        from("direct:Slushy.Reject.SendEmail")
                .routeId("Slushy.Reject.SendEmail")
                .setHeader("SlushyEmailTemplate").constant("reject")
                .to("direct:Slushy.SendEmail")
        ;

        from("direct:Slushy.Reject.MoveToTargetList")
                .routeId("Slushy.Reject.MoveToTargetList")
                .setHeader("SlushyTargetList", rejectConfig::getTargetList)
                .bean(moveCard)
        ;

    }

}
