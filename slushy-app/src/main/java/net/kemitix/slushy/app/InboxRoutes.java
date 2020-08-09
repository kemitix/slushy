package net.kemitix.slushy.app;

import net.kemitix.slushy.spi.InboxConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InboxRoutes
        extends RouteBuilder {

    private static final String ROUTING_SLIP = "Slushy.Inbox.RoutingSlip";

    @Inject InboxConfig inboxConfig;

    @Override
    public void configure() {
        fromF("timer:inbox?period=%s", inboxConfig.getPeriod())
                .routeId("Slushy.Inbox")
                .setBody(exchange -> InboxContext.empty())
                .setHeader(ROUTING_SLIP, inboxConfig::getRoutingSlip)
                .routingSlip(header(ROUTING_SLIP))
        ;
    }
}
