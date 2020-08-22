 package net.kemitix.slushy.app;

 import net.kemitix.slushy.spi.InboxConfig;
 import net.kemitix.slushy.spi.SlushyConfig;
 import org.apache.camel.builder.RouteBuilder;
 import org.apache.camel.builder.SimpleBuilder;
 import org.apache.camel.builder.ValueBuilder;

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
    @Inject AttachmentLoader attachmentLoader;
    @Inject ConversionService conversionService;
    @Inject EmailService emailService;
    @Inject SubmissionReceivedSubjectCreator subjectCreator;
    @Inject SubmissionReceivedBodyCreator bodyCreator;

    @Override
    public void configure() {
        fromF("timer:inbox?period=%s", inboxConfig.getScanPeriod())
                .routeId("Slushy.Inbox")
                .setBody(exchange -> trelloBoard.getInboxCards())
                .split(body())
                .setHeader("Slushy.RoutingSlip", inboxConfig::getRoutingSlip)
                .routingSlip(header("Slushy.RoutingSlip"))
        ;

        from("direct:Slushy.Inbox.Parse")
                .routeId("Slushy.Inbox.Parse")
                .setHeader("Slushy.Inbox.Card", body())
                .bean(submissionParser)
                .setHeader("Slushy.Inbox.Submission", body())
        ;

        from("direct:Slushy.Inbox.Reformat")
                .routeId("Slushy.Inbox.Reformat")
                .bean(cardFormatter, "reformat(" +
                        "${header[Slushy.Inbox.Submission]}, " +
                        "${header[Slushy.Inbox.Card]}" +
                        ")")
        ;

        from("direct:Slushy.Inbox.MoveToSlushPile")
                .routeId("Slushy.Inbox.MoveToSlushPile")
                .setHeader("Slushy.Inbox.Destination", trelloBoard::getSlush)
                .bean(cardMover, "move(" +
                        "${header[Slushy.Inbox.Card]}, " +
                        "${header[Slushy.Inbox.Destination]}" +
                        ")")
        ;

        from("direct:Slushy.Inbox.LoadAttachment")
                .routeId("Slushy.Inbox.LoadAttachment")
                .setHeader("Slushy.Inbox.Attachment", loadAttachment())
        ;

        from("direct:Slushy.Inbox.FormatForReader")
                .routeId("Slushy.Inbox.FormatForReader")
                .setHeader("Slushy.Inbox.Readable", convertAttachment())
        ;

        from("direct:Slushy.Inbox.SendToReader")
                .routeId("Slushy.Inbox.SendToReader")
                .setHeader("Slushy.Inbox.Recipient", slushyConfig::getReader)
                .setHeader("Slushy.Inbox.Sender", slushyConfig::getSender)
                .bean(emailService, "sendAttachmentOnly(" +
                        "${header[Slushy.Inbox.Recipient]}, " +
                        "${header[Slushy.Inbox.Sender]}, " +
                        "${header[Slushy.Inbox.Readable]}" +
                        ")")
        ;

        from("direct:Slushy.Inbox.SendEmailConfirmation")
                .routeId("Slushy.Inbox.SendEmailConfirmation")
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
                                ")"
                        )
        ;
    }

    private ValueBuilder loadAttachment() {
        return bean(attachmentLoader, "load(" +
                "${header[Slushy.Inbox.Card]}" +
                ")");
    }

    private ValueBuilder convertAttachment() {
        return bean(conversionService, "convert(" +
                "${header[Slushy.Inbox.Attachment]}" +
                ")");
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
