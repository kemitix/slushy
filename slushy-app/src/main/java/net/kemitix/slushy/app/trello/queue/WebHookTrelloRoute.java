package net.kemitix.slushy.app.trello.queue;

import net.kemitix.slushy.app.config.DynamicConfigConfig;
import net.kemitix.slushy.app.inbox.InboxConfig;
import net.kemitix.trello.LoadCard;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
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
                .setHeader("TranslationKey").jsonpath("body-json.action.display.translationKey")

                .choice()

                // updated card
                .when().simple("${header.ActionType} == 'updateCard'")
                .to("direct:Slushy.WebHook.Trello.UpdatedCard")
                .endChoice()

                // emailed cards
                .when().simple("${header.ActionType} == 'emailCard'")
                .to("direct:Slushy.WebHook.Trello.ActionEmailCard")
                .endChoice()

                // treat manually created cards as if they were emailed in
                .when().simple("${header.ActionType} == 'createCard'")
                .to("direct:Slushy.WebHook.Trello.ActionEmailCard")
                .endChoice()

                .when().simple("${header.TranslationKey} == 'action_move_card_from_list_to_list'")
                .to("direct:Slushy.WebHook.Trello.ActionMoveCardFromListToList")
                .endChoice()

                //.otherwise()
                //.log("Ignoring: ${header.ActionType} / ${header.TranslationKey}")

                .end()
        ;

        from("direct:Slushy.WebHook.Trello.UpdatedCard")
                .routeId("Slushy.WebHook.Trello.UpdatedCard")
                .setHeader("ListName").jsonpath("body-json.action.data.list.name")
                .setHeader("CardName").jsonpath("body-json.action.data.card.name")

                .choice()

                // updated config card
                .when().simple(updatedConfigCard())
                .to("direct:Slushy.Dynamic.Config.Update")
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

    private String updatedConfigCard() {
        var clauses = List.of(
                "${header.ListName} == '" + dynamicConfigConfig.getListName() + "'",
                "${header.CardName} == '" + dynamicConfigConfig.getCardName() + "'"
        );
        return String.join(" && ", clauses);
    }
}
