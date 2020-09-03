package net.kemitix.slushy.app.reject;

import net.kemitix.slushy.app.CardMover;
import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.RestedFilter;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.email.EmailService;
import net.kemitix.slushy.app.trello.TrelloBoard;
import net.kemitix.slushy.spi.SlushyConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.apache.camel.builder.ValueBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class RejectRoutes
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject RejectConfig rejectConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject CardMover cardMover;
    @Inject EmailService emailService;
    @Inject SubmissionRejectedEmailCreator emailCreator;
    @Inject RestedFilter restedFilter;
    @Inject Comments comments;

    @Override
    public void configure() {
        fromF("timer:reject?period=%s", rejectConfig.getScanPeriod())
                .routeId("Slushy.Reject")
                .setBody(exchange -> trelloBoard.getListCards(rejectConfig.getSourceList()))
                .split(body())
                .convertBodyTo(SlushyCard.class)
                .setHeader("Slushy.Reject.Age", rejectConfig::getRequiredAgeHours)
                .filter(bean(restedFilter, "isRested(${body}, ${header[Slushy.Reject.Age]})"))
                .setHeader("Slushy.RoutingSlip", rejectConfig::getRoutingSlip)
                .routingSlip(header("Slushy.RoutingSlip"))
        ;

        from("direct:Slushy.Reject.SendEmail")
                .routeId("Slushy.Reject.SendEmail")
                .setHeader("Slushy.Inbox.Recipient", submissionEmail())
                .setHeader("Slushy.Inbox.Sender", slushyConfig::getSender)
                .setHeader("Slushy.Inbox.Subject", subject())
                .setHeader("Slushy.Inbox.Body", bodyText())
                .setHeader("Slushy.Inbox.BodyHtml", bodyHtml())
                .bean(emailService, "send(" +
                        "${header[Slushy.Inbox.Recipient]}, " +
                        "${header[Slushy.Inbox.Sender]}, " +
                        "${header[Slushy.Inbox.Subject]}, " +
                        "${header[Slushy.Inbox.Body]}, " +
                        "${header[Slushy.Inbox.BodyHtml]}" +
                        ")")
                .setHeader("Slushy.Comment",
                        () -> "Sent rejection notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header[Slushy.Comment]}" +
                        ")")
        ;

        from("direct:Slushy.Reject.MoveToRejected")
                .routeId("Slushy.Reject.MoveToRejected")
                .setHeader("Slushy.TargetList", rejectConfig::getTargetList)
                .bean(cardMover, "move(" +
                        "${header.SlushyCard}, " +
                        "${header[Slushy.TargetList]}" +
                        ")")
        ;

    }

    private SimpleBuilder submissionEmail() {
        return simple("${header[Slushy.Inbox.Submission].email}");
    }

    private ValueBuilder bodyHtml() {
        return bean(emailCreator, "bodyHtml(" +
                "${header[Slushy.Inbox.Submission]}" +
                ")");
    }

    private ValueBuilder bodyText() {
        return bean(emailCreator, "bodyText(" +
                "${header[Slushy.Inbox.Submission]}" +
                ")");
    }

    private ValueBuilder subject() {
        return bean(emailCreator, "subject(" +
                "${header[Slushy.Inbox.Submission]}" +
                ")");
    }
}
