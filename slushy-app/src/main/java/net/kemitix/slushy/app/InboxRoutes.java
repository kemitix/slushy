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
    @Inject SubmissionParser submissionParser;
    @Inject CardFormatter cardFormatter;

    @Override
    public void configure() {
        fromF("timer:inbox?period=%s", inboxConfig.getScanPeriod())
                .routeId("Slushy.Inbox")
                .setBody(exchange -> inboxCards.get())
                .split(body())
                .setHeader(ROUTING_SLIP, inboxConfig::getRoutingSlip)
                .routingSlip(header(ROUTING_SLIP))
        ;

        from("direct:Slushy.Inbox.Parse")
                .routeId("Slushy.Inbox.Parse")
                .setHeader("Slushy.Inbox.Card", body())
                .bean(submissionParser)
        ;

        from("direct:Slushy.Inbox.Reformat")
                .routeId("Slushy.Inbox.Reformat")
                .bean(cardFormatter,
                        "reformat(${body}, ${header[Slushy.Inbox.Card]})")
        ;
    }
}
