package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.duedates.SetDueInDays;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InboxResponseDueByRoute
        extends RouteBuilder {

    private final SetDueInDays setDueInDays;
    private final InboxProperties inboxProperties;

    @Inject
    public InboxResponseDueByRoute(
            SetDueInDays setDueInDays,
            InboxProperties inboxProperties
    ) {
        this.setDueInDays = setDueInDays;
        this.inboxProperties = inboxProperties;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Inbox.ResponseDueBy")
                .routeId("Slushy.Inbox.ResponseDueBy")
                .setHeader("SlushyDueInDays", inboxProperties::dueDays)
                .bean(setDueInDays)
        ;
    }
}
