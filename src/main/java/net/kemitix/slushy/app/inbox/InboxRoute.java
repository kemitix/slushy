package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.withdraw.WithdrawConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InboxRoute
        extends RouteBuilder {
    private final InboxConfig inboxConfig;
    private final IsRequiredAge isRequiredAge;

    @Inject
    public InboxRoute(
            InboxConfig inboxConfig,
            IsRequiredAge isRequiredAge
    ) {
        this.inboxConfig = inboxConfig;
        this.isRequiredAge = isRequiredAge;
    }

    @Override
    public void configure() throws Exception {
        OnException.retry(this, inboxConfig);

        from("direct:Slushy.Card.Inbox")
                .routeId("Slushy.Card.Inbox")
                .log("Story arrived in Inbox")

                .setHeader("SlushyRequiredAge", inboxConfig::getRequiredAgeHours)
                .filter().method(isRequiredAge)

                .setHeader("SlushyRoutingSlip", inboxConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
