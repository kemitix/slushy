package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.OnException;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ReaderTimerRoute
        extends RouteBuilder {

    private final ReaderProperties readerProperties;
    private final TrelloBoard trelloBoard;

    @Inject
    public ReaderTimerRoute(
            DynamicReaderProperties readerProperties,
            TrelloBoard trelloBoard
    ) {
        this.readerProperties = readerProperties;
        this.trelloBoard = trelloBoard;
    }

    @Override
    public void configure() {
        OnException.retry(this, readerProperties);

        fromF("timer:reader?period=%s", readerProperties.scanPeriod())
                .routeId("Slushy.Reader.Timer")
                .to("direct:Slushy.Reader")
        ;
    }

}
