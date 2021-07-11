package net.kemitix.slushy.app.withdraw;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

// listens for card moves and accepts when card is moved into the Withdraw list
@ApplicationScoped
public class CardMovedWithdrawListenerRoute
        extends RouteBuilder {

    @Inject WithdrawProperties withdrawProperties;

    @Override
    public void configure() throws Exception {
        from("seda:Slushy.WebHook.CardMoved?multipleConsumers=true")
                .routeId("Slushy.WebHook.CardMoved.Withdrawn")
                .setHeader("ListWithdrawn", withdrawProperties::sourceList)
                .filter().simple("${header.SlushyMovedTo} == ${header.ListWithdrawn}")
                .to("direct:Slushy.Card.Withdrawn")
        ;
    }
}
