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
    private final TrelloConfig trelloConfig;
    private final ReaderProperties readerProperties;
    private final SendEmailAttachment sendEmailAttachment;
    private final AddComment addComment;

    @Inject
    public SendToReaderRoute(
            TrelloConfig trelloConfig,
            DynamicReaderProperties readerProperties,
            SendEmailAttachment sendEmailAttachment,
            AddComment addComment
    ) {
        this.trelloConfig = trelloConfig;
        this.readerProperties = readerProperties;
        this.sendEmailAttachment = sendEmailAttachment;
        this.addComment = addComment;
    }

    @Override
    public void configure() {
        from("direct:Slushy.SendToReader")
                .routeId("Slushy.SendToReader")

                .idempotentConsumer().simple("${header.SlushyCard.id}")
                .skipDuplicate(true)
                .idempotentRepository(memoryIdempotentRepository(readerProperties.maxSize()))

                .setHeader("SlushyRecipient", trelloConfig::getReader)
                .setHeader("SlushySender", trelloConfig::getSender)
                .setHeader("SlushySubject").simple("Reader: ${header.SlushyCard.name}")
                .bean(sendEmailAttachment)

                .setHeader("SlushyComment").constant("Sent attachment to reader")
                .bean(addComment)
        ;
    }
}
