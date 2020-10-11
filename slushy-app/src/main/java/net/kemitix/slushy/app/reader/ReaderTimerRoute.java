package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ReaderTimerRoute
        extends RouteBuilder {

    private final ReaderConfig readerConfig;
    private final TrelloBoard trelloBoard;

    @Inject
    public ReaderTimerRoute(
            ReaderConfig readerConfig,
            TrelloBoard trelloBoard
    ) {
        this.readerConfig = readerConfig;
        this.trelloBoard = trelloBoard;
    }

    @Override
    public void configure() {
        OnException.retry(this, readerConfig);

        fromF("timer:reader?period=%s", readerConfig.getScanPeriod())
                .routeId("Slushy.Reader")
                .setBody(exchange -> trelloBoard.getListCards(readerConfig.getSourceList()))
                .split(body())
                .setHeader("SlushyRoutingSlip", readerConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }

}
