package net.kemitix.slushy.app.hold;

import net.kemitix.slushy.app.duedates.SetDueInDays;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HoldDecisionDueByRoute
        extends RouteBuilder {

    private final SetDueInDays setDueInDays;
    private final HoldProperties holdProperties;

    @Inject
    public HoldDecisionDueByRoute(
            HoldProperties holdProperties,
            SetDueInDays setDueInDays
    ) {
        this.setDueInDays = setDueInDays;
        this.holdProperties = holdProperties;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Hold.DecisionDueBy")
                .routeId("Slushy.Hold.DecisionDueBy")
                .setHeader("SlushyDueInDays", holdProperties::dueDays)
                .bean(setDueInDays)
        ;
    }
}
