package net.kemitix.slushy.app;

import net.kemitix.slushy.spi.InboxConfig;
import net.kemitix.slushy.spi.RejectConfig;
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
    @Inject SubmissionRejectedSubjectCreator subjectCreator;
    @Inject SubmissionRejectedBodyCreator bodyCreator;
    @Inject RestedFilter restedFilter;

    @Override
    public void configure() {
        fromF("timer:reject?period=%s", rejectConfig.getScanPeriod())
                .routeId("Slushy.Reject")
                .setBody(exchange -> trelloBoard.getRejectCards())
                .split(body())
                .setHeader("Slushy.Reject.Age", rejectConfig::getRequiredAgeHours)
                .filter(bean(restedFilter, "isRested(${body}, ${header[Slushy.Reject.Age]})"))
                .setHeader("Slushy.RoutingSlip", rejectConfig::getRoutingSlip)
                .routingSlip(header("Slushy.RoutingSlip"))
        ;

        from("direct:Slushy.Reject.SendEmailRejection")
                .routeId("Slushy.Reject.SendEmailRejection")
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
        ;

        from("direct:Slushy.Reject.MoveToRejected")
                .routeId("Slushy.Reject.MoveToRejected")
                .setHeader("Slushy.Reject.Destination", trelloBoard::getRejected)
                .bean(cardMover, "move(" +
                        "${header[Slushy.Inbox.Card]}, " +
                        "${header[Slushy.Reject.Destination]}" +
                        ")")
        ;

    }

    private SimpleBuilder submissionEmail() {
        return simple("${header[Slushy.Inbox.Submission].email}");
    }

    private ValueBuilder bodyHtml() {
        return bean(bodyCreator, "bodyHtml(" +
                "${header[Slushy.Inbox.Submission]}" +
                ")");
    }

    private ValueBuilder bodyText() {
        return bean(bodyCreator, "bodyText(" +
                "${header[Slushy.Inbox.Submission]}" +
                ")");
    }

    private ValueBuilder subject() {
        return bean(subjectCreator, "subject(" +
                "${header[Slushy.Inbox.Submission]}" +
                ")");
    }
}
