package net.kemitix.slushy.app;

import net.kemitix.slushy.app.email.EmailService;
import net.kemitix.slushy.app.trello.TrelloBoard;
import net.kemitix.slushy.spi.HoldConfig;
import net.kemitix.slushy.spi.SlushyConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.apache.camel.builder.ValueBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class HoldRoutes
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject HoldConfig holdConfig;
    @Inject
    TrelloBoard trelloBoard;
    @Inject CardMover cardMover;
    @Inject
    EmailService emailService;
    @Inject SubmissionHoldEmailCreator emailCreator;
    @Inject RestedFilter restedFilter;
    @Inject Comments comments;

    @Override
    public void configure() {
        fromF("timer:hold?period=%s", holdConfig.getScanPeriod())
                .routeId("Slushy.Hold")
                .setBody(exchange -> trelloBoard.getHoldCards())
                .split(body())
                .setHeader("Slushy.Hold.Age", holdConfig::getRequiredAgeHours)
                .filter(bean(restedFilter, "isRested(${body}, ${header[Slushy.Hold.Age]})"))
                .setHeader("Slushy.RoutingSlip", holdConfig::getRoutingSlip)
                .routingSlip(header("Slushy.RoutingSlip"))
        ;

        from("direct:Slushy.Hold.SendEmailNotification")
                .routeId("Slushy.Hold.SendEmailNotification")
                .setHeader("Slushy.Email.Recipient", submissionEmail())
                .setHeader("Slushy.Email.Sender", slushyConfig::getSender)
                .setHeader("Slushy.Email.Subject", subject())
                .setHeader("Slushy.Email.Body", bodyText())
                .setHeader("Slushy.Email.BodyHtml", bodyHtml())
                .bean(emailService, "send(" +
                        "${header[Slushy.Email.Recipient]}, " +
                        "${header[Slushy.Email.Sender]}, " +
                        "${header[Slushy.Email.Subject]}, " +
                        "${header[Slushy.Email.Body]}, " +
                        "${header[Slushy.Email.BodyHtml]}" +
                        ")")
                .setHeader("Slushy.Comment",
                        () -> "Sent held notification to author")
                .bean(comments, "add(" +
                        "${header[Slushy.Inbox.Card]}, " +
                        "${header[Slushy.Comment]}" +
                        ")")
        ;

        from("direct:Slushy.Hold.MoveToHeld")
                .routeId("Slushy.Hold.MoveToHeld")
                .setHeader("Slushy.Hold.Destination", trelloBoard::getHeld)
                .bean(cardMover, "move(" +
                        "${header[Slushy.Inbox.Card]}, " +
                        "${header[Slushy.Hold.Destination]}" +
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
