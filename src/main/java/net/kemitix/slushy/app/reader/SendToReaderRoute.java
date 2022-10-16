package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.email.SendEmailAttachment;
import net.kemitix.trello.TrelloConfig;
import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository.memoryIdempotentRepository;

@ApplicationScoped
public class SendToReaderRoute
        extends RouteBuilder {
    private final DynamicReaderProperties readerProperties;
    private final TrelloConfig trelloConfig;
    private final SendEmailAttachment sendEmailAttachment;
    private final AddComment addComment;

    @Inject
    public SendToReaderRoute(
            DynamicReaderProperties readerProperties,
            TrelloConfig trelloConfig,
            SendEmailAttachment sendEmailAttachment,
            AddComment addComment
    ) {
        this.readerProperties = readerProperties;
        this.trelloConfig = trelloConfig;
        this.sendEmailAttachment = sendEmailAttachment;
        this.addComment = addComment;
    }

    @Override
    public void configure() {
        from("direct:Slushy.SendToReader")
                .routeId("Slushy.SendToReader")

                .setHeader("SlushyRecipient", readerProperties::reader)
                .setHeader("SlushySender", trelloConfig::getSender)
                .setHeader("SlushySubject").simple("Reader: ${header.SlushyCard.name}")
                .bean(sendEmailAttachment)

                .setHeader("SlushyComment").constant("Sent attachment to reader")
                .bean(addComment)
        ;
    }
}
