package net.kemitix.slushy.app.zeroattachment;

import net.kemitix.slushy.app.Attachment;
import net.kemitix.slushy.app.AttachmentLoader;
import net.kemitix.slushy.app.LocalAttachment;
import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.reader.ReaderConfig;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Scans the slush pile, listing stories that have a zero-length attachment.
 *
 * Only for use in release v1.12.0 then can be removed.
 */
@ApplicationScoped
public class ZeroLengthScanRoute
        extends RouteBuilder {

    @Inject TrelloBoard trelloBoard;
    @Inject ReaderConfig readerConfig;
    @Inject AttachmentLoader attachmentLoader;
    @Inject MoveCard moveCard;


    @Override
    public void configure() throws Exception {
        from("timer:zero-scan?repeatCount=1")
                .routeId("Slushy.ZeroLengthScan")

                .setHeader("SlushyTargetList")
                .constant(readerConfig.getSourceList())

                .setBody(exchange -> trelloBoard.getListCards(readerConfig.getTargetList()))
                .split(body())

                .setHeader("SlushyCard")
                .body()

                .setHeader("SlushyAttachment")
                .method(attachmentLoader)

                .choice()
                .when(simple("${header.SlushyAttachment.isZero}"))
                .bean(moveCard)
                .end()
        ;
    }

}
