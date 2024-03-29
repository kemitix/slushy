package net.kemitix.slushy.app.reader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;

// listens for card moves and accepts when card is added to the Reader list
@ApplicationScoped
public class CardMovedReaderListenerRoute
        extends RouteBuilder {

    @Inject
    DynamicReaderProperties readerProperties;

    @Override
    public void configure() throws Exception {
        from("seda:Slushy.WebHook.CardMoved?multipleConsumers=true")
                .routeId("Slushy.WebHook.CardMoved.Reader")
                .setHeader("ListName", readerProperties::sourceList)
                .filter().simple("${header.SlushyMovedTo} == ${header.ListName}")
                .log("Card '${header.SlushyCardName}' added to ${header.ListName}")
                .to("direct:Slushy.Reader")
        ;
    }
}
