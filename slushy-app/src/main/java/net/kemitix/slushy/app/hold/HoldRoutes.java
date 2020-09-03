package net.kemitix.slushy.app.hold;

import net.kemitix.slushy.app.CardMover;
import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.RestedFilter;
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
public class HoldRoutes
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject HoldConfig holdConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject CardMover cardMover;
    @Inject EmailService emailService;
    @Inject SubmissionHoldEmailCreator emailCreator;
    @Inject RestedFilter restedFilter;
    @Inject Comments comments;

    @Override
    public void configure() {
        fromF("timer:hold?period=%s", holdConfig.getScanPeriod())
                .routeId("Slushy.Hold")
                .setBody(exchange -> trelloBoard.getListCards(holdConfig.getHoldName()))
                .split(body())
                .setHeader("Slushy.Hold.Age", holdConfig::getRequiredAgeHours)
                .filter(bean(restedFilter, "isRested(${body}, ${header[Slushy.Hold.Age]})"))
                .setHeader("Slushy.RoutingSlip", holdConfig::getRoutingSlip)
                .routingSlip(header("Slushy.RoutingSlip"))
        ;

        from("direct:Slushy.Hold.SendEmail")
                .routeId("Slushy.Hold.SendEmail")
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
                        "${header.SlushyCard}, " +
                        "${header[Slushy.Comment]}" +
                        ")")
        ;

        from("direct:Slushy.Hold.MoveToHeld")
                .routeId("Slushy.Hold.MoveToHeld")
                .setHeader("Slushy.TargetList", holdConfig::getHeldName)
                .bean(cardMover, "move(" +
                        "${header.SlushyCard}, " +
                        "${header[Slushy.TargetList]}" +
                        ")")
        ;

    }

    private SimpleBuilder submissionEmail() {
        return simple("${header[SlushySubmission].email}");
    }

    private ValueBuilder bodyHtml() {
        return bean(emailCreator, "bodyHtml(" +
                "${header.SlushySubmission}" +
                ")");
    }

    private ValueBuilder bodyText() {
        return bean(emailCreator, "bodyText(" +
                "${header.SlushySubmission}" +
                ")");
    }

    private ValueBuilder subject() {
        return bean(emailCreator, "subject(" +
                "${header.SlushySubmission}" +
                ")");
    }
}
