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
                .setHeader("SlushyRoutingSlip", rejectConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;

        from("direct:Slushy.Reject.SendEmail")
                .routeId("Slushy.Reject.SendEmail")
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
                        () -> "Sent rejection notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")
        ;

        from("direct:Slushy.Reject.MoveToRejected")
                .routeId("Slushy.Reject.MoveToRejected")
                .setHeader("SlushyTargetList", rejectConfig::getTargetList)
                .bean(cardMover, "move(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyTargetList}" +
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
