package net.kemitix.slushy.app.slush;

import net.kemitix.slushy.app.reader.ReaderIsFull;
import net.kemitix.slushy.app.reader.ReaderProperties;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

// listens for card moves and accepts when card is removed from the Slush list
@ApplicationScoped
public class CardMovedSlushListenerRoute
        extends RouteBuilder {

    @Inject ReaderProperties readerConfig;
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
