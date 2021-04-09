package net.kemitix.slushy.app.email;

import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.OnException;
import net.kemitix.trello.TrelloConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SendEmailRoute
        extends RouteBuilder {

    @Inject
    TrelloConfig trelloConfig;
    @Inject EmailConfig emailConfig;
    @Inject SendEmail sendEmail;
    @Inject AddComment addComment;

    @Override
    public void configure() {
        OnException.retry(this, emailConfig);
        from("direct:Slushy.SendEmail")
                .routeId("Slushy.SendEmail")

                // don't send multiple copies of the same email to the same
                // recipient for the same card
                .idempotentConsumer().simple(
                "${header.SlushyEmailTemplate}" +
                        ":${header.SlushySubmission.email}" +
                        ":${header.SlushyCard.id}")
                .skipDuplicate(true)
                .messageIdRepository(MemoryIdempotentRepository::new)

                .setHeader("SlushyRecipient").simple("${header.SlushySubmission.email}")
                .setHeader("SlushySender", trelloConfig::getSender)
                .toD("velocity:net/kemitix/slushy/app/${header.SlushyEmailTemplate}/subject.txt")
                .setHeader("SlushySubject").body()
                .toD("velocity:net/kemitix/slushy/app/${header.SlushyEmailTemplate}/body.txt")
                .setHeader("SlushyBody").body()
                .toD("velocity:net/kemitix/slushy/app/${header.SlushyEmailTemplate}/body.html")
                .setHeader("SlushyBodyHtml").body()
                .bean(sendEmail)

                .setHeader("SlushyComment").simple("Sent ${header.SlushyEmailTemplate} email to author")
                .bean(addComment)
        ;
    }
}
