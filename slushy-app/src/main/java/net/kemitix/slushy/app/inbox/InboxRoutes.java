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
                .setHeader("SlushyTargetList", inboxConfig::getTargetList)
                .bean(cardMover, "move(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyTargetList}" +
                        ")")
        ;

        from("direct:Slushy.LoadAttachment")
                .routeId("Slushy.LoadAttachment")
                .setHeader("SlushyAttachment", loadAttachment())
        ;

        from("direct:Slushy.FormatForReader")
                .routeId("Slushy.FormatForReader")
                .setHeader("SlushyReadableAttachment", convertAttachment())
        ;

        from("direct:Slushy.SendToReader")
                .routeId("Slushy.SendToReader")
                .setHeader("SlushyRecipient", slushyConfig::getReader)
                .setHeader("SlushySender", slushyConfig::getSender)
                .bean(emailService, "sendAttachmentOnly(" +
                        "${header.SlushyRecipient}, " +
                        "${header.SlushySender}, " +
                        "${header.SlushyReadableAttachment}" +
                        ")")
                .setHeader("SlushyComment",
                        () -> "Sent attachment to reader")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")
        ;

        from("direct:Slushy.Inbox.SendEmailConfirmation")
                .routeId("Slushy.Inbox.SendEmailConfirmation")
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

    private ValueBuilder loadAttachment() {
        return bean(attachmentLoader, "load(" +
                "${header.SlushyCard}" +
                ")");
    }

    private ValueBuilder convertAttachment() {
        return bean(conversionService, "convert(" +
                "${header.SlushyAttachment}" +
                ")");
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
