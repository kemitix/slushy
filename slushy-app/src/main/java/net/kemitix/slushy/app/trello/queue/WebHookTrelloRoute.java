package net.kemitix.slushy.app.trello.queue;

import net.kemitix.slushy.app.trello.LoadCard;
import net.kemitix.slushy.app.trello.TrelloBoard;
import net.kemitix.slushy.app.withdraw.WithdrawConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WebHookTrelloRoute
        extends RouteBuilder {

    private final WithdrawConfig withdrawnConfig;
    private final LoadCard loadCard;

    @Inject
    public WebHookTrelloRoute(
            WithdrawConfig withdrawnConfig,
            LoadCard loadCard
    ) {
        this.withdrawnConfig = withdrawnConfig;
        this.loadCard = loadCard;

    }

    @Override
    public void configure() throws Exception {
        from("direct:Slushy.WebHook.Trello")
                .routeId("Slushy.WebHook.Trello")

                .idempotentConsumer()
                .jsonpath("body-json.action.id")
                .skipDuplicate(true)
                .messageIdRepository(MemoryIdempotentRepository::new)

                .log("WebHook message from Trello")


                .choice()

                .when().jsonpath("body-json.action.display[?(@.translationKey == 'action_move_card_from_list_to_list')]")
                .to("direct:Slushy.WebHook.Trello.CardMovedFromListToList")

                .otherwise()
                .setHeader("actionKey").jsonpath("body-json.action.display.translationKey")
                .process(exchange ->
                        log.info(String.format("Action Key: %s", exchange.getIn().getHeader("ActionKey"))))

                .endChoice()
        ;

        from("direct:Slushy.WebHook.Trello.CardMovedFromListToList")
                .routeId("Slushy.WebHook.Trello.CardMovedFromListToList")
                .setHeader("SlushyCardName").jsonpath("body-json.action.data.card.name")

                .choice()

                .when().jsonpath("body-json.action.data[?(@.listBefore empty false && @.listAfter empty false)]")
                .setHeader("SlushyMovedFrom").jsonpath("body-json.action.data.listBefore.name")
                .setHeader("SlushyMovedTo").jsonpath("body-json.action.data.listAfter.name")
                .log("Moved Card '${header.SlushyCardName}' from '${header.SlushyMovedFrom}' to '${header.SlushyMovedTo}'")
                .to("direct:Slushy.WebHook.CardMoved")

                .otherwise()
                .log("Card moved, but listBefore and/or listAfter not given")
                .to("direct:Slushy.WebHook.Unhandled")

                .endChoice()
        ;

        from("direct:Slushy.WebHook.Unhandled")
                .routeId("Slushy.WebHook.Unhandled")
                .log("Unhandled WebHook event")
        ;

        from("direct:Slushy.WebHook.CardMoved")
                .routeId("Slushy.WebHook.CardMoved")

                .setHeader("SlushyCardId").jsonpath("body-json.action.data.card.id")
                .setBody().method(loadCard)

                .setHeader("ListWithdrawn").constant(withdrawnConfig.getSourceList())

                .choice()

                .when().simple("${header.SlushyMovedTo} == ${header.ListWithdrawn}")
                .to("direct:Slushy.Card.Withdrawn")

                .endChoice()

        ;
    }
}
