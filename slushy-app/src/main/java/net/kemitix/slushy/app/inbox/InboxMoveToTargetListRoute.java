package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.MoveCard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InboxMoveToTargetListRoute
        extends RouteBuilder {

    private final InboxConfig inboxConfig;
    private final MoveCard moveCard;

    @Inject
    public InboxMoveToTargetListRoute(
            InboxConfig inboxConfig,
            MoveCard moveCard
    ) {
        this.inboxConfig = inboxConfig;
        this.moveCard = moveCard;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Inbox.MoveToTargetList")
                .routeId("Slushy.Inbox.MoveToTargetList")
                .setHeader("SlushyTargetList", inboxConfig::getTargetList)
                .bean(moveCard)
        ;
    }
}
