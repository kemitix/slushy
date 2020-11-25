package net.kemitix.slushy.app.slush;

import net.kemitix.slushy.app.reader.ReaderConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

// listens for card moves and accepts when card is removed from the Slush list
@ApplicationScoped
public class CardMovedSlushListenerRoute
        extends RouteBuilder {

    @Inject ReaderConfig readerConfig;

    @Override
    public void configure() throws Exception {
        from("seda:Slushy.WebHook.CardMoved?multipleConsumers=true")
                .routeId("Slushy.WebHook.CardMoved.Slush")
                .setHeader("ListSlush").constant(readerConfig.getTargetList())
                .filter().simple("${header.SlushyMovedFrom} == ${header.ListSlush}")
                .log("Card '${header.SlushyCardName}' removed from ${header.ListSlush}")
                .to("direct:Slushy.Reader")
        ;
    }
}
