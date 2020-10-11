package net.kemitix.slushy.app.reject;

import net.kemitix.slushy.app.MoveCard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RejectMoveToTargetListRoute
        extends RouteBuilder {

    private final RejectConfig rejectConfig;
    private final MoveCard moveCard;

    @Inject
    public RejectMoveToTargetListRoute(
            RejectConfig rejectConfig,
            MoveCard moveCard
    ) {
        this.rejectConfig = rejectConfig;
        this.moveCard = moveCard;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Reject.MoveToTargetList")
                .routeId("Slushy.Reject.MoveToTargetList")
                .setHeader("SlushyTargetList", rejectConfig::getTargetList)
                .bean(moveCard)
        ;
    }
}
