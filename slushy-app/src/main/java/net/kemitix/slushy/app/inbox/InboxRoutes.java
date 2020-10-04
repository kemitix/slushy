package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.ParseSubmission;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class InboxRoutes
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject InboxConfig inboxConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject ParseSubmission parseSubmission;
    @Inject ReformatCard reformatCard;
    @Inject MoveCard moveCard;
    @Inject IsRequiredAge isRequiredAge;

    @Override
    public void configure() {
        OnException.retry(this, inboxConfig);

        fromF("timer:inbox?period=%s", inboxConfig.getScanPeriod())
                .routeId("Slushy.Inbox")

                .setBody(exchange -> trelloBoard.getListCards(inboxConfig.getSourceList()))
                .split(body())

                .setHeader("SlushyRequiredAge", inboxConfig::getRequiredAgeHours)
                .filter(bean(isRequiredAge))

                .setHeader("SlushyRoutingSlip", inboxConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;

        from("direct:Slushy.CardToSubmission")
                .routeId("Slushy.CardToSubmission")
                .setHeader("SlushyCard", body())
                .bean(parseSubmission)
                .setHeader("SlushySubmission", body())
        ;

        from("direct:Slushy.ReformatCard")
                .routeId("Slushy.ReformatCard")
                .bean(reformatCard)
        ;

        from("direct:Slushy.Inbox.MoveToTargetList")
                .routeId("Slushy.Inbox.MoveToTargetList")
                .setHeader("SlushyTargetList", inboxConfig::getTargetList)
                .bean(moveCard)
        ;

        from("direct:Slushy.Inbox.SendEmailConfirmation")
                .routeId("Slushy.Inbox.SendEmailConfirmation")
                .setHeader("SlushyEmailTemplate").constant("inbox")
                .to("direct:Slushy.SendEmail")
        ;
    }

}
