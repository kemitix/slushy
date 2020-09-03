 package net.kemitix.slushy.app.inbox;

 import net.kemitix.slushy.app.AttachmentLoader;
 import net.kemitix.slushy.app.CardMover;
 import net.kemitix.slushy.app.Comments;
 import net.kemitix.slushy.app.email.EmailService;
 import net.kemitix.slushy.app.fileconversion.ConversionService;
 import net.kemitix.slushy.app.trello.TrelloBoard;
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
    @Inject SubmissionReceivedEmailCreator emailCreator;
    @Inject Comments comments;

    @Override
    public void configure() {
        fromF("timer:inbox?period=%s", inboxConfig.getScanPeriod())
                .routeId("Slushy.Inbox")
                .setBody(exchange -> trelloBoard.getListCards(inboxConfig.getSourceList()))
                .split(body())
                .setHeader("SlushyRoutingSlip", inboxConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;

        from("direct:Slushy.Parse")
                .routeId("Slushy.Parse")
                .setHeader("SlushyCard", body())
                .bean(submissionParser)
                .setHeader("SlushySubmission", body())
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
                .setHeader("Slushy.TargetList", inboxConfig::getTargetList)
                .bean(cardMover, "move(" +
                        "${header.SlushyCard}, " +
                        "${header[Slushy.TargetList]}" +
                        ")")
        ;

        from("direct:Slushy.LoadAttachment")
                .routeId("Slushy.LoadAttachment")
                .setHeader("Slushy.Inbox.Attachment", loadAttachment())
        ;

        from("direct:Slushy.FormatForReader")
                .routeId("Slushy.FormatForReader")
                .setHeader("Slushy.Inbox.Readable", convertAttachment())
        ;

        from("direct:Slushy.SendToReader")
                .routeId("Slushy.SendToReader")
                .setHeader("Slushy.Inbox.Recipient", slushyConfig::getReader)
                .setHeader("Slushy.Inbox.Sender", slushyConfig::getSender)
                .bean(emailService, "sendAttachmentOnly(" +
                        "${header[Slushy.Inbox.Recipient]}, " +
                        "${header[Slushy.Inbox.Sender]}, " +
                        "${header[Slushy.Inbox.Readable]}" +
                        ")")
                .setHeader("Slushy.Comment",
                        () -> "Sent attachment to reader")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header[Slushy.Comment]}" +
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
                .setHeader("Slushy.Comment",
                        () -> "Sent received notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header[Slushy.Comment]}" +
                        ")")
        ;
    }

    private ValueBuilder loadAttachment() {
        return bean(attachmentLoader, "load(" +
                "${header.SlushyCard}" +
                ")");
    }

    private ValueBuilder convertAttachment() {
        return bean(conversionService, "convert(" +
                "${header[Slushy.Inbox.Attachment]}" +
                ")");
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
