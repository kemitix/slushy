package net.kemitix.slushy.app.trello.queue;

import net.kemitix.slushy.app.inbox.InboxConfig;
import net.kemitix.slushy.app.trello.LoadCard;
import net.kemitix.slushy.app.withdraw.WithdrawConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WebHookTrelloRoute
        extends RouteBuilder {

    private final InboxConfig inboxConfig;
    private final WithdrawConfig withdrawnConfig;
    private final LoadCard loadCard;

    @Inject
    public WebHookTrelloRoute(
            InboxConfig inboxConfig,
            WithdrawConfig withdrawnConfig,
            LoadCard loadCard
    ) {
        this.inboxConfig = inboxConfig;
        this.withdrawnConfig = withdrawnConfig;
        this.loadCard = loadCard;

    }

    @Override
    public void configure() throws Exception {
        from("direct:Slushy.WebHook.Trello")
                .routeId("Slushy.WebHook.Trello")

                .idempotentConsumer().jsonpath("body-json.action.id")
                .skipDuplicate(true)
                .messageIdRepository(MemoryIdempotentRepository::new)

                .setHeader("Action").jsonpath("body-json.action.display.translationKey")

                .choice()
                .when().simple("${header.Action} != 'unknown'")
                .log("WebHook message from Trello: ${header.Action}")
                .end()

                .choice()

                .when().simple("${header.Action} == 'action_email_card'")
                .to("direct:Slushy.WebHook.Trello.ActionEmailCard")
                .endChoice()

                .when().simple("${header.Action} == 'action_move_card_from_list_to_list'")
                .to("direct:Slushy.WebHook.Trello.ActionMoveCardFromListToList")
                .endChoice()

                //.otherwise()
                //.choice()
                //.when().simple("${header.Action} != 'unknown'")
                //.log("Unknown action: ${header.Action}")
                //.end()
//                .to("direct:Slushy.WebHook.Unhandled")

                .end()
        ;

        from("direct:Slushy.WebHook.Trello.ActionEmailCard")
                .routeId("Slushy.WebHook.Trello.ActionEmailCard")

                .setHeader("SlushyCardName").jsonpath("body-json.action.data.card.name")

                .setHeader("ListName").jsonpath("body-json.action.data.list.name")

                .setHeader("ListInbox").constant(inboxConfig.getSourceList())

                .log("Card Created in '${header.ListName}': '${header.SlushyCardName}'")

                .setHeader("SlushyCardId").jsonpath("body-json.action.data.card.id")

                .doTry()
                .setBody().method(loadCard)

                .choice()

                .when().simple("${header.ListName} == ${header.ListInbox}")
                .to("direct:Slushy.Card.Inbox")

                .otherwise()
                .log("Dropping unexpected email sent to: ${header.ListName}")
//                .to("direct:Slushy.WebHook.Unhandled")

                .endChoice()

                .endDoTry()
                .doCatch(NullPointerException.class)
        ;

        from("direct:Slushy.WebHook.Trello.ActionMoveCardFromListToList")
                .routeId("Slushy.WebHook.Trello.ActionMoveCardFromListToList")

                .setHeader("SlushyCardName").jsonpath("body-json.action.data.card.name")

                .setHeader("ListBefore").jsonpath("body-json.action.data.listBefore")
                .setHeader("ListAfter").jsonpath("body-json.action.data.listAfter")

                .choice()

                .when().simple("${header.ListBefore} != '' && ${header.ListAfter} != ''")
                .setHeader("SlushyMovedFrom").jsonpath("body-json.action.data.listBefore.name")
                .setHeader("SlushyMovedTo").jsonpath("body-json.action.data.listAfter.name")
                .log("Moved Card '${header.SlushyCardName}' from '${header.SlushyMovedFrom}' to '${header.SlushyMovedTo}'")
                .to("direct:Slushy.WebHook.CardMoved")

                .otherwise()
                .log("Card moved, but listBefore and/or listAfter not given")
//                .to("direct:Slushy.WebHook.Unhandled")

                .endChoice()
        ;

//        from("direct:Slushy.WebHook.Unhandled")
//                .routeId("Slushy.WebHook.Unhandled")
//        //.log("Dropping Unhandled WebHook event")
//        //.process(exchange ->
//        //        log.info(exchange.getIn().getBody(String.class)))
//        ;

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
