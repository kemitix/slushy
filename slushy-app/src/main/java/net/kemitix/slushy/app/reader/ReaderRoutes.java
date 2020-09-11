package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.AttachmentLoader;
import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.OnException;
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
    @Inject MoveCard moveCard;
    @Inject AttachmentLoader attachmentLoader;
    @Inject SendEmailAttachment sendEmailAttachment;
    @Inject AddComment addComment;

    @Override
    public void configure() {
        OnException.retry(this, readerConfig);

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

                .setHeader("SlushyComment")
                .constant("Sent attachment to reader")
                .bean(addComment)
        ;


        from("direct:Slushy.Reader.MoveToTargetList")
                .routeId("Slushy.Reader.MoveToTargetList")
                .setHeader("SlushyTargetList", readerConfig::getTargetList)
                .bean(moveCard)
        ;
    }

}
