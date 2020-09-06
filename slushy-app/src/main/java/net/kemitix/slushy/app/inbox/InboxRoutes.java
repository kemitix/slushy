package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.SubmissionParser;
import net.kemitix.slushy.app.email.SendEmail;
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
    @Inject SubmissionParser submissionParser;
    @Inject ReformatCard reformatCard;
    @Inject MoveCard moveCard;
    @Inject SendEmail sendEmail;
    @Inject AddComment addComment;
    @Inject IsRequiredAge isRequiredAge;

    @Override
    public void configure() {
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
                .bean(submissionParser)
                .setHeader("SlushySubmission", body())
        ;

        from("direct:Slushy.ValidateAttachment")
                .routeId("Slushy.ValidateAttachment")
                .choice()
                .when(simple("${body.isValid}"))
                .otherwise()
                .to("direct:Slushy.InvalidAttachment")
                .process(exchange -> exchange.setRouteStop(true))
                .end()
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

                .setHeader("SlushyRecipient").simple(
                        "${header.SlushySubmission.email}")
                .setHeader("SlushySender", slushyConfig::getSender)
                .to("velocity:net/kemitix/slushy/app/inbox/subject.txt")
                .setHeader("SlushySubject").body()
                .to("velocity:net/kemitix/slushy/app/inbox/body.txt")
                .setHeader("SlushyBody").body()
                .to("velocity:net/kemitix/slushy/app/inbox/body.html")
                .setHeader("SlushyBodyHtml").body()
                .bean(sendEmail)

                .setHeader("SlushyComment").simple(
                        "Sent received notification to author")
                .bean(addComment)
        ;
    }

}
