package net.kemitix.slushy.app.hold;

import net.kemitix.slushy.app.duedates.SetDueInDays;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HoldDecisionDueByRoute
        extends RouteBuilder {

    private final SetDueInDays setDueInDays;
    private final HoldConfig holdConfig;

    @Inject
    public HoldDecisionDueByRoute(
            SetDueInDays setDueInDays,
            HoldConfig holdConfig
    ) {
        this.setDueInDays = setDueInDays;
        this.holdConfig = holdConfig;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Hold.DecisionDueBy")
                .routeId("Slushy.Hold.DecisionDueBy")
                .setHeader("SlushyDueInDays")
                .constant(constant(holdConfig.getDueDays()))
                .bean(setDueInDays)
        ;
    }
}
