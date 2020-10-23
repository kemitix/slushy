package net.kemitix.slushy.app.trello.webhook;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RegisterWebhookRoute
        extends RouteBuilder {

    private final RegisterWebhook registerWebhook;

    @Inject
    public RegisterWebhookRoute(RegisterWebhook registerWebhook) {
        this.registerWebhook = registerWebhook;
    }

    @Override
    public void configure() {
        from("timer:trello-webhook?repeatCount=1")
                .routeId("trello-webhook")
                .bean(registerWebhook)
        ;
    }

}
