package net.kemitix.slushy.app.inbox;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.MoveCard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Supplier;

@Log
@ApplicationScoped
public class InboxMoveToTargetListRoute
        extends RouteBuilder {

    private final InboxProperties inboxProperties;
    private final MoveCard moveCard;

    @Inject
    public InboxMoveToTargetListRoute(
            DynamicInboxProperties inboxProperties,
            MoveCard moveCard
    ) {
        this.inboxProperties = inboxProperties;
        this.moveCard = moveCard;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Inbox.MoveToTargetList")
                .routeId("Slushy.Inbox.MoveToTargetList")
                .setHeader("SlushyTargetList", getTargetListSupplier())
                .bean(moveCard)
        ;
    }

    private Supplier<Object> getTargetListSupplier() {
        return () -> {
            String targetList = inboxProperties.targetList();
            log.info("GetTargetList = " + targetList);
            return targetList;
        };
    }
}
