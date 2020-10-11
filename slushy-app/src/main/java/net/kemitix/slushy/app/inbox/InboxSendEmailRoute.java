package net.kemitix.slushy.app.inbox;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InboxSendEmailRoute
        extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:Slushy.Inbox.SendEmailConfirmation")
                .routeId("Slushy.Inbox.SendEmailConfirmation")
                .setHeader("SlushyEmailTemplate").constant("inbox")
                .to("direct:Slushy.SendEmail")
        ;
    }
}
