package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.CardMover;
import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.RestedFilter;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.SubmissionParser;
import net.kemitix.slushy.app.email.EmailService;
import net.kemitix.slushy.app.templating.SlushyTemplate;
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
    @Inject
    SubmissionParser submissionParser;
    @Inject CardFormatter cardFormatter;
    @Inject CardMover cardMover;
    @Inject EmailService emailService;
    @Inject Comments comments;
    @Inject RestedFilter restedFilter;
    @Inject SlushyTemplate slushyTemplate;

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

        from("direct:Slushy.Parse")
                .routeId("Slushy.Parse")
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
                .setHeader("SlushyRecipient",
                        simple("${header.SlushySubmission.email}"))
                .setHeader("SlushySender", slushyConfig::getSender)

                .setHeader("SlushyTemplateModel").header("SlushySubmission")

                .setHeader("SlushyTemplateName").simple("inbox/subject.txt")
                .setHeader("SlushySubject", bean(slushyTemplate))

                .setHeader("SlushyTemplateName", simple("inbox/body.txt"))
                .setHeader("SlushyBody", bean(slushyTemplate))

                .setHeader("SlushyTemplateName", simple("inbox/body.html"))
                .setHeader("SlushyBodyHtml", bean(slushyTemplate))

                .bean(emailService, "send(" +
                                "${header.SlushyRecipient}, " +
                                "${header.SlushySender}, " +
                                "${header.SlushySubject}, " +
                                "${header.SlushyBody}, " +
                                "${header.SlushyBodyHtml}" +
                                ")"
                        )
                .setHeader("SlushyComment",
                        () -> "Sent received notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")
        ;
    }

}
