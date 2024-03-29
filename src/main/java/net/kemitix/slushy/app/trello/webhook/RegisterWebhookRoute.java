package net.kemitix.slushy.app.trello.webhook;

import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RegisterWebhookRoute
        extends RouteBuilder {

    @Inject
    private RegisterWebhook registerWebhook;

    @Override
    public void configure() {
        from("timer:trello-webhook?repeatCount=1")
                .routeId("trello-webhook")
                .bean(registerWebhook)
        ;
    }

}
