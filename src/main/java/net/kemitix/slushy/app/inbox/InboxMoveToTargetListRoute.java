package net.kemitix.slushy.app.inbox;

import lombok.Getter;
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
                .setHeader("SlushyTargetList", getGetTargetList())
                .bean(moveCard)
        ;
    }

    private Supplier<Object> getGetTargetList() {
        return () -> {
            String targetList = inboxConfig.getTargetList();
            log.info("GetTargetList = " + targetList);
            return targetList;
        };
    }
}
