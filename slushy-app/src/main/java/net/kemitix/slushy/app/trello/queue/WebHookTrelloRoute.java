package net.kemitix.slushy.app.trello.queue;

import net.kemitix.slushy.app.inbox.InboxConfig;
import net.kemitix.trello.LoadCard;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WebHookTrelloRoute
        extends RouteBuilder {

    private final InboxConfig inboxConfig;
    private final LoadCard loadCard;

    @Inject
    public WebHookTrelloRoute(
            InboxConfig inboxConfig,
            LoadCard loadCard
    ) {
        this.inboxConfig = inboxConfig;
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

                .setHeader("SlushyCardId").jsonpath("body-json.action.data.card.id")
                .setBody().method(loadCard)

                .log("Card Created in '${header.ListName}': '${header.SlushyCardName}'")

                .setHeader("ListInbox").constant(inboxConfig.getSourceList())
                .filter().simple("${header.ListName} == ${header.ListInbox}")
                .to("direct:Slushy.Card.Inbox")
        ;

        from("direct:Slushy.WebHook.Trello.ActionMoveCardFromListToList")
                .routeId("Slushy.WebHook.Trello.ActionMoveCardFromListToList")

                .setHeader("SlushyCardName").jsonpath("body-json.action.data.card.name")

                .setHeader("ListBefore").jsonpath("body-json.action.data.listBefore")
                .setHeader("ListAfter").jsonpath("body-json.action.data.listAfter")

                .filter().simple("${header.ListBefore} != '' && ${header.ListAfter} != ''")

                .setHeader("SlushyMovedFrom").jsonpath("body-json.action.data.listBefore.name")
                .setHeader("SlushyMovedTo").jsonpath("body-json.action.data.listAfter.name")

                .setHeader("SlushyCardId").jsonpath("body-json.action.data.card.id")
                .setBody().method(loadCard)

                .log("Moved Card '${header.SlushyCardName}' from '${header.SlushyMovedFrom}' to '${header.SlushyMovedTo}'")
                .to("seda:Slushy.WebHook.CardMoved")
        ;

//        from("direct:Slushy.WebHook.Unhandled")
//                .routeId("Slushy.WebHook.Unhandled")
//        //.log("Dropping Unhandled WebHook event")
//        //.process(exchange ->
//        //        log.info(exchange.getIn().getBody(String.class)))
//        ;
    }
}
