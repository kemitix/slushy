 package net.kemitix.slushy.app.inbox;

 import net.kemitix.slushy.app.AttachmentLoader;
 import net.kemitix.slushy.app.CardMover;
 import net.kemitix.slushy.app.Comments;
 import net.kemitix.slushy.app.email.EmailService;
 import net.kemitix.slushy.app.fileconversion.ConversionService;
 import net.kemitix.slushy.app.trello.TrelloBoard;
 import net.kemitix.slushy.spi.SlushyConfig;
 import org.apache.camel.CamelContext;
 import org.apache.camel.builder.RouteBuilder;
 import org.apache.camel.builder.SimpleBuilder;
 import org.apache.camel.builder.ValueBuilder;

 import javax.enterprise.context.ApplicationScoped;
 import javax.inject.Inject;

 import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class InboxRoutes
        extends RouteBuilder {

    @Inject CamelContext camelContext;
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
                .setBody(exchange -> trelloBoard.getInboxCards())
                .split(body())
                .setHeader("Slushy.RoutingSlip", inboxConfig::getRoutingSlip)
                .routingSlip(header("Slushy.RoutingSlip"))
        ;

        from("direct:Slushy.Parse")
                .routeId("Slushy.Parse")
                .setHeader("Slushy.Inbox.Card", body())
                .bean(submissionParser)
                .setHeader("Slushy.Inbox.Submission", body())
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
                        "${header[Slushy.Inbox.Submission]}, " +
                        "${header[Slushy.Inbox.Card]}" +
                        ")")
        ;

        from("direct:Slushy.Inbox.MoveToTargetList")
                .routeId("Slushy.Inbox.MoveToTargetList")
                .setHeader("Slushy.TargetList", inboxConfig::getTargetList)
                .bean(cardMover, "move(" +
                        "${header[Slushy.Inbox.Card]}, " +
                        "${header[Slushy.TargetList]}" +
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
                .setHeader("Slushy.Comment",
                        () -> "Sent attachment to reader")
                .bean(comments, "add(" +
                        "${header[Slushy.Inbox.Card]}, " +
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
                        "${header[Slushy.Inbox.Card]}, " +
                        "${header[Slushy.Comment]}" +
                        ")")
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
