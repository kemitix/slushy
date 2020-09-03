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
    @Inject CardMover cardMover;
    @Inject TrelloBoard trelloBoard;

    @Override
    public void configure() throws Exception {
        from("direct:Slushy.Reader.MoveToTargetList")
                .routeId("Slushy.Reader.MoveToTargetList")
                .setHeader("Slushy.TargetList", readerConfig::getTargetList)
                .bean(cardMover, "move(" +
                        "${header[Slushy.Inbox.Card]}, " +
                        "${header[Slushy.TargetList]}" +
                        ")")
        ;
    }
}
