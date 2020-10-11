package net.kemitix.slushy.app.duedates;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DueIn30DaysRoute
        extends RouteBuilder {
    private final SetDueInDays setDueInDays;

    @Inject
    public DueIn30DaysRoute(SetDueInDays setDueInDays) {
        this.setDueInDays = setDueInDays;
    }

    @Override
    public void configure() throws Exception {
        from("direct:Slushy.DueIn30Days")
                .routeId("Slushy.DueIn30Days")
                .setHeader("SlushyDueInDays")
                .constant("30")
                .bean(setDueInDays)
        ;
    }
}
