package net.kemitix.slushy.app.trello.queue;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WebHookTrelloRoute
        extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:Slushy.WebHook.Trello")
                .routeId("Slushy.WebHook.Trello")

                .idempotentConsumer()
                .jsonpath("body-json.action.id")
                .skipDuplicate(true)
                .messageIdRepository(MemoryIdempotentRepository::new)

                .log("WebHook message from Trello")

                .process(exchange ->
                        log.info(exchange.getIn().getBody(String.class)))

                .choice()

                .when().jsonpath("body-json.action.display[?(@.translationKey == 'action_move_card_from_list_to_list')]")
                .to("direct:Slushy.WebHook.Trello.MoveCard")

                .otherwise()
                .setHeader("actionKey").jsonpath("body-json.action.display.translationKey")
                .process(exchange ->
                        log.info(String.format("Action Key: %s", exchange.getIn().getHeader("ActionKey"))))
                .end()
        ;

        from("direct:Slushy.WebHook.Trello.MoveCard")
                .routeId("Slushy.WebHook.Trello.MoveCard")
                .setHeader("SlushyCardName").jsonpath("body-json.action.data.card.name")
                .setHeader("SlushyMovedFrom").jsonpath("body-json.action.data.listBefore.name")
                .setHeader("SlushyMovedTo").jsonpath("body-json.action.data.listAfter.name")
                .log("Moved Card '${header.SlushyCardName}' from '${header.SlushyMovedFrom}' to '${header.SlushyMovedTo}'")
        ;
    }
}
