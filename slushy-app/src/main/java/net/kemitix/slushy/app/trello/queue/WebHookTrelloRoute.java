package net.kemitix.slushy.app.trello.queue;

import net.kemitix.slushy.app.config.DynamicConfigConfig;
import net.kemitix.slushy.app.inbox.InboxConfig;
import net.kemitix.trello.LoadCard;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class WebHookTrelloRoute
        extends RouteBuilder {

    private final InboxConfig inboxConfig;
    private final LoadCard loadCard;
    private final DynamicConfigConfig dynamicConfigConfig;

    @Inject
    public WebHookTrelloRoute(
            InboxConfig inboxConfig,
            LoadCard loadCard,
            DynamicConfigConfig dynamicConfigConfig
    ) {
        this.inboxConfig = inboxConfig;
        this.loadCard = loadCard;
        this.dynamicConfigConfig = dynamicConfigConfig;
    }

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("direct:dropInvalidWebHookMessage"));

        from("direct:dropInvalidWebHookMessage")
                .log("Dropping Invalid Webhook message: ${body}")
        ;

        from("direct:Slushy.WebHook.Trello")
                .routeId("Slushy.WebHook.Trello")

                .idempotentConsumer().jsonpath("body-json.action.id")
                .skipDuplicate(true)
                .messageIdRepository(MemoryIdempotentRepository::new)

                .setHeader("ActionType").jsonpath("body-json.action.type")

                .choice()

                // updated card
                .when().simple("${header.ActionType} == 'updateCard'")
                .to("direct:Slushy.WebHook.Trello.UpdatedCard")
                .endChoice()

                // emailed cards
                .when().simple("${header.ActionType} == 'emailCard'")
                .to("direct:Slushy.WebHook.Trello.ActionEmailCard")
                .to("direct:Slushy.Status.Update")
                .endChoice()

                // treat manually created cards as if they were emailed in
                .when().simple("${header.ActionType} == 'createCard'")
                .to("direct:Slushy.WebHook.Trello.ActionEmailCard")
                .endChoice()

                .otherwise()
                .log("Ignoring: ${header.ActionType}")

                .end()
        ;

        from("direct:Slushy.WebHook.Trello.UpdatedCard")
                .routeId("Slushy.WebHook.Trello.UpdatedCard")
                .setHeader("ListName").jsonpath("body-json.action.data.list.name", true)
                .setHeader("CardName").jsonpath("body-json.action.data.card.name", true)
                .setHeader("ListBefore").jsonpath("body-json.action.data.listBefore.name", true)
                .setHeader("ListAfter").jsonpath("body-json.action.data.listAfter.name", true)

                .choice()

                // updated config card
                .when().simple(updatedConfigCard())
                .to("direct:Slushy.Dynamic.Config.Update")
                .endChoice()

                // moved between lists
                .when().simple("${header.ListBefore} != ${header.ListAfter}")
                .to("direct:Slushy.WebHook.Trello.ActionMoveCardFromListToList")
                .endChoice()

                .end()
        ;

        from("direct:Slushy.WebHook.Trello.ActionEmailCard")
                .routeId("Slushy.WebHook.Trello.ActionEmailCard")

                .setHeader("SlushyCardName").jsonpath("body-json.action.data.card.name")

                .setHeader("ListName").jsonpath("body-json.action.data.list.name")

                .setHeader("SlushyCardId").jsonpath("body-json.action.data.card.id")
                .setBody().method(loadCard)

                .log("Card Created in '${header.ListName}': '${header.SlushyCardName}'")

                .setHeader("ListInbox", inboxConfig::getSourceList)
                .filter().simple("${header.ListName} == ${header.ListInbox}")
                .to("direct:Slushy.Card.Inbox")
        ;

        from("direct:Slushy.WebHook.Trello.ActionMoveCardFromListToList")
                .routeId("Slushy.WebHook.Trello.ActionMoveCardFromListToList")

                .setHeader("SlushyCardName").jsonpath("body-json.action.data.card.name")

                .filter().simple("${header.ListBefore} != '' && ${header.ListAfter} != ''")

                .setHeader("SlushyMovedFrom").header("ListBefore")
                .setHeader("SlushyMovedTo").header("ListAfter")

                .setHeader("SlushyCardId").jsonpath("body-json.action.data.card.id")
                .setBody().method(loadCard)

                .log("Moved Card '${header.SlushyCardName}' from '${header.SlushyMovedFrom}' to '${header.SlushyMovedTo}'")

                .setHeader("ListInbox", inboxConfig::getSourceList)

                .choice()
                .when().simple("${header.ListAfter} == ${header.ListInbox}")
                .log("Moved to inbox")
                .to("direct:Slushy.Card.Inbox")
                .endChoice()

                .otherwise()
                .to("seda:Slushy.WebHook.CardMoved")
                .end()
        ;

//        from("direct:Slushy.WebHook.Unhandled")
//                .routeId("Slushy.WebHook.Unhandled")
//        //.log("Dropping Unhandled WebHook event")
//        //.process(exchange ->
//        //        log.info(exchange.getIn().getBody(String.class)))
//        ;
    }

    private String updatedConfigCard() {
        var clauses = List.of(
                "${header.ListName} == '" + dynamicConfigConfig.getListName() + "'",
                "${header.CardName} == '" + dynamicConfigConfig.getCardName() + "'"
        );
        return String.join(" && ", clauses);
    }
}
