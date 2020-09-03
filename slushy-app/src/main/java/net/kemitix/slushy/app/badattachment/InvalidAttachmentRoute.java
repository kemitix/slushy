package net.kemitix.slushy.app.badattachment;

import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.email.EmailService;
import net.kemitix.slushy.spi.SlushyConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.apache.camel.builder.ValueBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class InvalidAttachmentRoute
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject EmailService emailService;
    @Inject Comments comments;
    @Inject InvalidAttachmentEmailCreator emailCreator;

    @Override
    public void configure() {
        from("direct:Slushy.InvalidAttachment")
                .routeId("Slushy.InvalidAttachment")
                .log("Submission rejected due to an unsupported file type")
                // send email to author
                .setHeader("Slushy.Inbox.Recipient", submissionEmail())
                .setHeader("Slushy.Inbox.Sender", slushyConfig::getSender)
                .setHeader("Slushy.Inbox.Subject", subject())
                .setHeader("Slushy.Inbox.Body", bodyText())
                .setHeader("Slushy.Inbox.BodyHtml", bodyHtml())
                .bean(emailService,
                        "send(" +
                                "${header[Slushy.Inbox.Recipient]}, " +
                                "${header[Slushy.Inbox.Sender]}, " +
                                "${header[Slushy.Inbox.Subject]}, " +
                                "${header[Slushy.Inbox.Body]}, " +
                                "${header[Slushy.Inbox.BodyHtml]})")
                .setHeader("Slushy.Comment",
                        () -> "Sent invalid attachment rejection notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header[Slushy.Comment]}" +
                        ")")
                // move card to rejected
                .to("direct:Slushy.Reject.MoveToRejected")
        ;
    }

    private SimpleBuilder submissionEmail() {
        return simple("${header.SlushySubmission.email}");
    }

    private ValueBuilder bodyHtml() {
        return bean(emailCreator, "bodyHtml(${body})");
    }

    private ValueBuilder bodyText() {
        return bean(emailCreator, "bodyText(${body})");
    }

    private ValueBuilder subject() {
        return bean(emailCreator, "subject(${body})");
    }

}
