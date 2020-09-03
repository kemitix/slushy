package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.CardMover;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ReaderRoutes
        extends RouteBuilder {

    @Inject ReaderConfig readerConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject CardMover cardMover;

    @Override
    public void configure() {
        fromF("timer:reader?period=%s", readerConfig.getScanPeriod())
                .routeId("Slushy.Reader")
                .setBody(exchange -> trelloBoard.getListCards(readerConfig.getSourceList()))
                .split(body())
                .setHeader("SlushyRoutingSlip", readerConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;

        from("direct:Slushy.Reader.MoveToTargetList")
                .routeId("Slushy.Reader.MoveToTargetList")
                .setHeader("Slushy.TargetList", readerConfig::getTargetList)
                .bean(cardMover, "move(" +
                        "${header.SlushyCard}, " +
                        "${header[Slushy.TargetList]}" +
                        ")")
        ;
    }
}
