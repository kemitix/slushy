package net.kemitix.slushy.app.reject;

import net.kemitix.slushy.app.MoveCard;
import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RejectMoveToTargetListRoute
        extends RouteBuilder {

    private final RejectProperties rejectProperties;
    private final MoveCard moveCard;

    @Inject
    public RejectMoveToTargetListRoute(
            DynamicRejectProperties rejectProperties,
            MoveCard moveCard
    ) {
        this.rejectProperties = rejectProperties;
        this.moveCard = moveCard;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Reject.MoveToTargetList")
                .routeId("Slushy.Reject.MoveToTargetList")
                .setHeader("SlushyTargetList", rejectProperties::targetList)
                .bean(moveCard)
        ;
    }
}
