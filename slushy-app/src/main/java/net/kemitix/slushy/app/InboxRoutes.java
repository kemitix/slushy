package net.kemitix.slushy.app;

import net.kemitix.slushy.spi.InboxConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class InboxRoutes
        extends RouteBuilder {

    private static final String ROUTING_SLIP = "Slushy.Inbox.RoutingSlip";

    @Inject InboxConfig inboxConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject SubmissionParser submissionParser;
    @Inject CardFormatter cardFormatter;
    @Inject CardMover cardMover;
    @Inject AttachmentLoader attachmentLoader;
    @Inject ConversionService conversionService;
    @Inject EmailService emailService;

    @Override
    public void configure() {
        fromF("timer:inbox?period=%s", inboxConfig.getScanPeriod())
                .routeId("Slushy.Inbox")
                .setBody(exchange -> trelloBoard.getInboxCards())
                .split(body())
                .setHeader(ROUTING_SLIP, inboxConfig::getRoutingSlip)
                .routingSlip(header(ROUTING_SLIP))
        ;

        from("direct:Slushy.Inbox.Parse")
                .routeId("Slushy.Inbox.Parse")
                .setHeader("Slushy.Inbox.Card", body())
                .bean(submissionParser)
        ;

        from("direct:Slushy.Inbox.Reformat")
                .routeId("Slushy.Inbox.Reformat")
                .bean(cardFormatter,
                        "reformat(${body}, ${header[Slushy.Inbox.Card]})")
        ;

        from("direct:Slushy.Inbox.MoveToSlushPile")
                .routeId("Slushy.Inbox.MoveToSlushPile")
                .setHeader("Slushy.Inbox.Destination", trelloBoard::getSlush)
                .bean(cardMover, "move(${header[Slushy.Inbox.Card]}, ${header[Slushy.Inbox.Destination]})")
        ;

        from("direct:Slushy.Inbox.LoadAttachment")
                .routeId("Slushy.Inbox.LoadAttachment")
                .setHeader("Slushy.Inbox.Attachment",
                        bean(attachmentLoader,
                                "load(${header[Slushy.Inbox.Card]})"))
        ;

        from("direct:Slushy.Inbox.FormatForReader")
                .routeId("Slushy.Inbox.FormatForReader")
                .setHeader("Slushy.Inbox.ReadableAttachment",
                        bean(conversionService, "convert(${header[Slushy.Inbox.Attachment]})"))
        ;

        from("direct:Slushy.Inbox.SendToReader")
                .routeId("direct:Slushy.Inbox.SendToReader")
                .setHeader("Slushy.Inbox.Sender", inboxConfig::getSender)
                .setHeader("Slushy.Inbox.Recipient", inboxConfig::getRecipient)
                .bean(emailService, "send(${header[Slushy.Inbox.Attachment]}, " +
                        "${header[Slushy.Inbox.Sender]}, $header[Slushy.Inbox.Recipient])")
        ;
    }
}
