package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.email.SendEmailAttachment;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SendToReaderRoute
        extends RouteBuilder {
    private final SlushyConfig slushyConfig;
    private final SendEmailAttachment sendEmailAttachment;
    private final AddComment addComment;

    @Inject
    public SendToReaderRoute(
            SlushyConfig slushyConfig,
            SendEmailAttachment sendEmailAttachment,
            AddComment addComment
    ) {
        this.slushyConfig = slushyConfig;
        this.sendEmailAttachment = sendEmailAttachment;
        this.addComment = addComment;
    }

    @Override
    public void configure() {
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
    }
}
