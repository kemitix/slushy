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
                .setHeader("SlushyRequiredAge", holdConfig::getRequiredAgeHours)
                .filter(bean(restedFilter, "isRested(${body}, ${header.SlushyRequiredAge})"))
                .setHeader("SlushyRoutingSlip", holdConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;

        from("direct:Slushy.Hold.SendEmail")
                .routeId("Slushy.Hold.SendEmail")
                .setHeader("SlushyRecipient", submissionEmail())
                .setHeader("SlushySender", slushyConfig::getSender)
                .setHeader("SlushySubject", subject())
                .setHeader("SlushyBody", bodyText())
                .setHeader("SlushyBodyHtml", bodyHtml())
                .bean(emailService, "send(" +
                        "${header.SlushyRecipient}, " +
                        "${header.SlushySender}, " +
                        "${header.SlushySubject}, " +
                        "${header.SlushyBody}, " +
                        "${header.SlushyBodyHtml}" +
                        ")")
                .setHeader("SlushyComment",
                        () -> "Sent held notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")
        ;

        from("direct:Slushy.Hold.MoveToHeld")
                .routeId("Slushy.Hold.MoveToHeld")
                .setHeader("SlushyTargetList", holdConfig::getHeldName)
                .bean(cardMover, "move(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyTargetList}" +
                        ")")
        ;

    }

    private SimpleBuilder submissionEmail() {
        return simple("${header.SlushySubmission.email}");
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
