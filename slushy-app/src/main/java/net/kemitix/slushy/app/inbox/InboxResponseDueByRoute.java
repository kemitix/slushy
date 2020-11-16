package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.duedates.SetDueInDays;
import net.kemitix.slushy.app.inbox.InboxConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InboxResponseDueByRoute
        extends RouteBuilder {

    private final SetDueInDays setDueInDays;
    private final InboxConfig inboxConfig;

    @Inject
    public InboxResponseDueByRoute(
            SetDueInDays setDueInDays,
            InboxConfig inboxConfig
    ) {
        this.setDueInDays = setDueInDays;
        this.inboxConfig = inboxConfig;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Inbox.ResponseDueBy")
                .routeId("Slushy.Inbox.ResponseDueBy")
                .setHeader("SlushyDueInDays")
                .constant(constant(inboxConfig.getDueDays()))
                .bean(setDueInDays)
        ;
    }
}
