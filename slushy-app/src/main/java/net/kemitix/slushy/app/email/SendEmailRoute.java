package net.kemitix.slushy.app.email;

import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.SlushyConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SendEmailRoute
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject EmailConfig emailConfig;
    @Inject SendEmail sendEmail;
    @Inject AddComment addComment;

    @Override
    public void configure() {
        OnException.retry(this, emailConfig);
        from("direct:Slushy.SendEmail")
                .routeId("Slushy.SendEmail")

                .setHeader("SlushyRecipient").simple("${header.SlushySubmission.email}")
                .setHeader("SlushySender").constant(slushyConfig.getSender())
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
