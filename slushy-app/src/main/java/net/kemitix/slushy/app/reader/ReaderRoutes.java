package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.AttachmentLoader;
import net.kemitix.slushy.app.CardMover;
import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.email.SendEmailAttachment;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class ReaderRoutes
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject ReaderConfig readerConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject CardMover cardMover;
    @Inject AttachmentLoader attachmentLoader;
    @Inject SendEmailAttachment sendEmailAttachment;
    @Inject Comments comments;

    @Override
    public void configure() {
        fromF("timer:reader?period=%s", readerConfig.getScanPeriod())
                .routeId("Slushy.Reader")
                .setBody(exchange -> trelloBoard.getListCards(readerConfig.getSourceList()))
                .split(body())
                .setHeader("SlushyRoutingSlip", readerConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;

        from("direct:Slushy.LoadAttachment")
                .routeId("Slushy.LoadAttachment")
                .setHeader("SlushyAttachment",
                        bean(attachmentLoader, "load(${header.SlushyCard})"))
        ;

        from("direct:Slushy.SendToReader")
                .routeId("Slushy.SendToReader")

                .setHeader("SlushyRecipient", slushyConfig::getReader)
                .setHeader("SlushySender", slushyConfig::getSender)
                .setHeader("SlushySubject", simple("Reader: ${header.SlushyCard.name}"))
                .bean(sendEmailAttachment)

                .setHeader("SlushyComment", () -> "Sent attachment to reader")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")
        ;


        from("direct:Slushy.Reader.MoveToTargetList")
                .routeId("Slushy.Reader.MoveToTargetList")
                .setHeader("SlushyTargetList", readerConfig::getTargetList)
                .bean(cardMover, "move(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyTargetList}" +
                        ")")
        ;
    }

}
