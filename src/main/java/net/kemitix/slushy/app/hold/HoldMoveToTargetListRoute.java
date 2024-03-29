package net.kemitix.slushy.app.hold;

import net.kemitix.slushy.app.MoveCard;
import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HoldMoveToTargetListRoute
        extends RouteBuilder {

    private final HoldProperties holdProperties;
    private final MoveCard moveCard;

    @Inject
    public HoldMoveToTargetListRoute(
            DynamicHoldProperties holdProperties,
            MoveCard moveCard
    ) {
        this.holdProperties = holdProperties;
        this.moveCard = moveCard;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Hold.MoveToTargetList")
                .routeId("Slushy.Hold.MoveToTargetList")
                .setHeader("SlushyTargetList", holdProperties::targetList)
                .bean(moveCard)
        ;
    }
}
