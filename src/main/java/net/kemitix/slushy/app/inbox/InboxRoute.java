package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InboxRoute
        extends RouteBuilder {
    private final InboxProperties inboxProperties;
    private final IsRequiredAge isRequiredAge;

    @Inject
    public InboxRoute(
            DynamicInboxProperties inboxProperties,
            IsRequiredAge isRequiredAge
    ) {
        this.inboxProperties = inboxProperties;
        this.isRequiredAge = isRequiredAge;
    }

    @Override
    public void configure() throws Exception {
        OnException.retry(this, inboxProperties);

        from("direct:Slushy.Card.Inbox")
                .routeId("Slushy.Card.Inbox")
                .log("Story arrived in Inbox")

                .setHeader("SlushyRequiredAge", inboxProperties::requiredAgeHours)
                .filter().method(isRequiredAge)

                .setHeader("SlushyRoutingSlip", inboxProperties::routingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
