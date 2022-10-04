package net.kemitix.slushy.app.trello.queue;

import net.kemitix.slushy.app.config.ConfigProperties;
import net.kemitix.slushy.app.inbox.DynamicInboxProperties;
import net.kemitix.slushy.trello.CardLoader;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class WebHookTrelloRoute
        extends RouteBuilder {

    @Inject
    DynamicInboxProperties inboxProperties;
    @Inject
    CardLoader cardLoader;
    @Inject
    ConfigProperties configProperties;

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
                .idempotentRepository(new MemoryIdempotentRepository())

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
                .to("direct:Slushy.Status.Update")
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
                .to("direct:Slushy.Status.Update")
                .endChoice()

                .end()
        ;

        from("direct:Slushy.WebHook.Trello.ActionEmailCard")
                .routeId("Slushy.WebHook.Trello.ActionEmailCard")

                .setHeader("SlushyCardName").jsonpath("body-json.action.data.card.name")

                .setHeader("ListName").jsonpath("body-json.action.data.list.name")

                .setHeader("SlushyCardId").jsonpath("body-json.action.data.card.id")
                .setBody().method(cardLoader)

                .log("Card Created in '${header.ListName}': '${header.SlushyCardName}'")

                .setHeader("ListInbox", inboxProperties::sourceList)
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
                .setBody().method(cardLoader)

                .log("Moved Card '${header.SlushyCardName}' from '${header.SlushyMovedFrom}' to '${header.SlushyMovedTo}'")

                .setHeader("ListInbox", inboxProperties::sourceList)

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
                "${header.ListName} == '" + configProperties.listName() + "'",
                "${header.CardName} == '" + configProperties.cardName() + "'"
        );
        return String.join(" && ", clauses);
    }
}
