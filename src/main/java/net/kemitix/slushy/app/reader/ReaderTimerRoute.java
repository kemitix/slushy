package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.trello.SlushyBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ReaderTimerRoute
        extends RouteBuilder {

    @Inject
    DynamicReaderProperties readerProperties;
    @Inject
    SlushyBoard slushyBoard;

    @Override
    public void configure() {
        OnException.retry(this, readerProperties);

        fromF("timer:reader?period=%s", readerProperties.scanPeriod())
                .routeId("Slushy.Reader.Timer")
                .to("direct:Slushy.Reader")
        ;
    }

}
