package net.kemitix.slushy.app.email;

import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.SlushyConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SendEmailRoute
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject SendEmail sendEmail;
    @Inject AddComment addComment;

    @Override
    public void configure() throws Exception {
        from("direct:SlushySendEmail")
                .routeId("SlushySendEmail")

                .setHeader("SlushyRecipient").simple("${header.SlushySubmission.email}")
                .setHeader("SlushySender").constant(slushyConfig.getSender())
                .toF("velocity:net/kemitix/slushy/app/%s/subject.txt", simple("${header.SlushyEmailTemplate}"))
                .setHeader("SlushySubject").body()
                .toF("velocity:net/kemitix/slushy/app/%s/body.txt", simple("${header.SlushyEmailTemplate}"))
                .setHeader("SlushyBody").body()
                .toF("velocity:net/kemitix/slushy/app/%s/body.html", simple("${header.SlushyEmailTemplate}"))
                .setHeader("SlushyBodyHtml").body()
                .bean(sendEmail)

                .setHeader("SlushyComment").simple("Sent ${header.SlushyEmailTemplate} email to author")
                .bean(addComment)
        ;
    }
}
