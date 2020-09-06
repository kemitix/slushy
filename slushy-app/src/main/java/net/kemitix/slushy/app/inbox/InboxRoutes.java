package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.CardMover;
import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.RestedFilter;
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
    @Inject CardFormatter cardFormatter;
    @Inject CardMover cardMover;
    @Inject SendEmail sendEmail;
    @Inject Comments comments;
    @Inject RestedFilter restedFilter;

    @Override
    public void configure() {
        fromF("timer:inbox?period=%s", inboxConfig.getScanPeriod())
                .routeId("Slushy.Inbox")

                .setBody(exchange -> trelloBoard.getListCards(inboxConfig.getSourceList()))
                .split(body())

                .setHeader("SlushyRequiredAge", inboxConfig::getRequiredAgeHours)
                .filter(bean(restedFilter, "isRested(${body}, ${header.SlushyRequiredAge})"))

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

        from("direct:Slushy.Reformat")
                .routeId("Slushy.Reformat")
                .bean(cardFormatter, "reformat(" +
                        "${header.SlushySubmission}, " +
                        "${header.SlushyCard}" +
                        ")")
        ;

        from("direct:Slushy.Inbox.MoveToTargetList")
                .routeId("Slushy.Inbox.MoveToTargetList")
                .setHeader("SlushyTargetList", inboxConfig::getTargetList)
                .bean(cardMover, "move(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyTargetList}" +
                        ")")
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
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")
        ;
    }

}
