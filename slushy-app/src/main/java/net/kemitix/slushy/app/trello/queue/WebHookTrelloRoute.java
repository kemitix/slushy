package net.kemitix.slushy.app.trello.queue;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WebHookTrelloRoute
        extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:Slushy.WebHook.Trello")
                .routeId("Slushy.WebHook.Trello")
                .log("SOURCE is Trello")

                .process(exchange ->
                        log.info(exchange.getIn().getBody(String.class)))
        ;
    }
}
