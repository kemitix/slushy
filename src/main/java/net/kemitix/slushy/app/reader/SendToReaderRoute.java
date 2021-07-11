package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.AddComment;
import net.kemitix.trello.TrelloConfig;
import net.kemitix.slushy.app.email.SendEmailAttachment;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
            DynamicReaderConfig readerConfig,
            SendEmailAttachment sendEmailAttachment,
            AddComment addComment
    ) {
        this.trelloConfig = trelloConfig;
        this.readerProperties = readerConfig;
        this.sendEmailAttachment = sendEmailAttachment;
        this.addComment = addComment;
    }

    @Override
    public void configure() {
        from("direct:Slushy.SendToReader")
                .routeId("Slushy.SendToReader")

                .idempotentConsumer().simple("${header.SlushyCard.id}")
                .skipDuplicate(true)
                .messageIdRepository(() ->
                        memoryIdempotentRepository(readerProperties.maxSize()))

                .setHeader("SlushyRecipient", trelloConfig::getReader)
                .setHeader("SlushySender", trelloConfig::getSender)
                .setHeader("SlushySubject").simple("Reader: ${header.SlushyCard.name}")
                .bean(sendEmailAttachment)

                .setHeader("SlushyComment").constant("Sent attachment to reader")
                .bean(addComment)
        ;
    }
}
