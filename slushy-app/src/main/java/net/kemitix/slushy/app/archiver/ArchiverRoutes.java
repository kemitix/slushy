package net.kemitix.slushy.app.archiver;

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
public class ArchiverRoutes
        extends RouteBuilder {

    @Inject ArchiverConfig archiverConfig;
    @Inject ArchiveCard archiveCard;
    @Inject CamelContext camelContext;

    @Override
    public void configure() {
        OnException.retry(this, archiverConfig);

        TemplatedRouteBuilder.builder(camelContext, ROUTE_LIST_PROCESS)
                .parameter(PARAM_NAME, "archiver")
                .parameter(PARAM_SCAN_PERIOD, archiverConfig.getScanPeriod())
                .parameter(PARAM_LIST_NAME, archiverConfig.getSourceList())
                .parameter(PARAM_REQUIRED_AGE_HOURS, archiverConfig.getRequiredAgeHours())
                .parameter(PARAM_ROUTING_SLIP, archiverConfig.getRoutingSlip())
                .add()
        ;

        from("direct:Slushy.ArchiveCard")
                .routeId("Slushy.ArchiveCard")
                .bean(archiveCard)
        ;
    }
}
