package net.kemitix.slushy.app;

import net.kemitix.slushy.app.trello.GetListCards;
import org.apache.camel.builder.RouteBuilder;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
@Priority(1)
public class ListProcessRouteTemplate
        extends RouteBuilder {

    public static final String ROUTE_LIST_PROCESS = "ListProcess";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_SCAN_PERIOD = "scan-period";
    public static final String PARAM_LIST_NAME = "list-name";
    public static final String PARAM_REQUIRED_AGE_HOURS = "required-age-hours";
    public static final String PARAM_ROUTING_SLIP = "routing-slip";

    @Inject IsRequiredAge isRequiredAge;
    @Inject GetListCards getListCards;

    @Override
    public void configure() throws Exception {
        routeTemplate(ROUTE_LIST_PROCESS)
                .templateParameter(PARAM_NAME)
                .templateParameter(PARAM_SCAN_PERIOD)
                .templateParameter(PARAM_LIST_NAME)
                .templateParameter(PARAM_REQUIRED_AGE_HOURS)
                .templateParameter(PARAM_ROUTING_SLIP)

                .from("timer:{{name}}?period={{scan-period}}")
                .routeId("Slushy.{{name}}")
                .log("Process list Start: {{name}}")

                .setHeader("SlushyListName").simple("{{list-name}}")
                .bean(getListCards)
                .split(body())

                .setHeader("SlushyRequiredAge").simple("{{required-age-hours}}")
                .filter(bean(isRequiredAge))

                .setHeader("SlushyRoutingSlip").simple("{{routing-slip}}")
                .routingSlip(header("SlushyRoutingSlip"))

                .log("Process list Finish: {{name}}")
        ;

    }
}
