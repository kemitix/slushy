package net.kemitix.slushy.app.badattachment;

import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.ValidFileTypes;
import net.kemitix.slushy.app.email.SendEmail;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InvalidAttachmentRoute
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject SendEmail sendEmail;
    @Inject Comments comments;
    @Inject ValidFileTypes validFileTypes;

    @Override
    public void configure() {
        from("direct:Slushy.InvalidAttachment")
                .routeId("Slushy.InvalidAttachment")
                .log("Submission rejected due to an unsupported file type")

                .setHeader("SlushyValidFileTypes", () ->
                        String.join(", ", validFileTypes.get()))

                // send email to author
                .setHeader("SlushyRecipient").simple("${header.SlushySubmission.email}")
                .setHeader("SlushySender", slushyConfig::getSender)
                .to("velocity:net/kemitix/slushy/app/badattachment/subject.txt")
                .setHeader("SlushySubject").body()
                .to("velocity:net/kemitix/slushy/app/badattachment/body.txt")
                .setHeader("SlushyBody").body()
                .to("velocity:net/kemitix/slushy/app/badattachment/body.html")
                .setHeader("SlushyBodyHtml").body()
                .bean(sendEmail)

                .setHeader("SlushyComment").simple(
                        "Sent invalid attachment rejection notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")

                // move card to rejected
                .to("direct:Slushy.Reject.MoveToTargetList")
        ;
    }

}
