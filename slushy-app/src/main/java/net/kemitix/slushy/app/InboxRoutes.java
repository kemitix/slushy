package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
import net.kemitix.slushy.spi.InboxConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Supplier;

@ApplicationScoped
public class InboxRoutes
        extends RouteBuilder {

    private static final String ROUTING_SLIP = "Slushy.Inbox.RoutingSlip";

    @Inject InboxConfig inboxConfig;
    @Inject @Inbox Supplier<List<Card>> inboxCards;

    @Override
    public void configure() {
        fromF("timer:inbox?period=%s", inboxConfig.getScanPeriod())
                .routeId("Slushy.Inbox")
                .setHeader(ROUTING_SLIP, inboxConfig::getRoutingSlip)
                .routingSlip(header(ROUTING_SLIP))
        ;

        from("direct:Slushy.Inbox.Load")
                .routeId("Slushy.Inbox.Load")
                .setBody(exchange -> inboxCards.get())
                .split(body())
                .to("log:load");
    }
}
