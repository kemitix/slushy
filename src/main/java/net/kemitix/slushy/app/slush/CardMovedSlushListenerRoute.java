package net.kemitix.slushy.app.slush;

import net.kemitix.slushy.app.reader.DynamicReaderProperties;
import net.kemitix.slushy.app.reader.ReaderIsFull;
import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

// listens for card moves and accepts when card is removed from the Slush list
@ApplicationScoped
public class CardMovedSlushListenerRoute
        extends RouteBuilder {

    @Inject
    DynamicReaderProperties readerConfig;
    @Inject ReaderIsFull readerIsFull;

    @Override
    public void configure() throws Exception {
        from("seda:Slushy.WebHook.CardMoved?multipleConsumers=true")
                .routeId("Slushy.WebHook.CardMoved.Slush")
                .setHeader("ListSlush", readerConfig::targetList)
                .filter().simple("${header.SlushyMovedFrom} == ${header.ListSlush}")
                .log("Card '${header.SlushyCardName}' removed from ${header.ListSlush}")
                .bean(readerIsFull, "reset")
                .to("direct:Slushy.Reader")
        ;
    }
}
