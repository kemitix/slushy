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
    private final ReaderConfig readerConfig;
    private final SendEmailAttachment sendEmailAttachment;
    private final AddComment addComment;

    @Inject
    public SendToReaderRoute(
            TrelloConfig trelloConfig,
            ReaderConfig readerConfig,
            SendEmailAttachment sendEmailAttachment,
            AddComment addComment
    ) {
        this.trelloConfig = trelloConfig;
        this.readerConfig = readerConfig;
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
                        memoryIdempotentRepository(readerConfig.getMaxSize()))

                .setHeader("SlushyRecipient").constant(trelloConfig.getReader())
                .setHeader("SlushySender").constant(trelloConfig.getSender())
                .setHeader("SlushySubject").simple("Reader: ${header.SlushyCard.name}")
                .bean(sendEmailAttachment)

                .setHeader("SlushyComment").constant("Sent attachment to reader")
                .bean(addComment)
        ;
    }
}
