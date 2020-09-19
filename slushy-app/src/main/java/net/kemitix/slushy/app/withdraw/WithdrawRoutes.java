package net.kemitix.slushy.app.withdraw;

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
public class WithdrawRoutes
        extends RouteBuilder {

    @Inject WithdrawConfig withdrawConfig;
    @Inject CamelContext camelContext;

    @Override
    public void configure() {
        OnException.retry(this, withdrawConfig);

        TemplatedRouteBuilder.builder(camelContext, ROUTE_LIST_PROCESS)
                .parameter(PARAM_NAME, "reader")
                .parameter(PARAM_SCAN_PERIOD, withdrawConfig.getScanPeriod())
                .parameter(PARAM_LIST_NAME, withdrawConfig.getSourceList())
                .parameter(PARAM_REQUIRED_AGE_HOURS, withdrawConfig.getRequiredAgeHours())
                .parameter(PARAM_ROUTING_SLIP, withdrawConfig.getRoutingSlip())
                .add()
        ;

        from("direct:Slushy.Withdraw.SendEmail")
                .routeId("Slushy.Withdraw.SendEmail")
                .setHeader("SlushyEmailTemplate").constant("withdraw")
                .to("direct:Slushy.SendEmail")
        ;
    }

}
