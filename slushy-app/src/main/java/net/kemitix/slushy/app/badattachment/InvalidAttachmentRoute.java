package net.kemitix.slushy.app.badattachment;

import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.email.EmailService;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.templating.SlushyTemplate;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class InvalidAttachmentRoute
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject EmailService emailService;
    @Inject Comments comments;
    @Inject SlushyTemplate slushyTemplate;
    @Inject BadAttachmentModelBuilder badAttachmentModelBuilder;

    @Override
    public void configure() {
        from("direct:Slushy.InvalidAttachment")
                .routeId("Slushy.InvalidAttachment")
                .log("Submission rejected due to an unsupported file type")
                // send email to author
                .setHeader("SlushyRecipient").simple("${header.SlushySubmission.email}")
                .setHeader("SlushySender", slushyConfig::getSender)

                .setHeader("SlushyTemplateModel", bean(badAttachmentModelBuilder))

                .setHeader("SlushyTemplateName").simple("badattachment/subject.txt")
                .setHeader("SlushySubject", bean(slushyTemplate))

                .setHeader("SlushyTemplateName", simple("badattachment/body.txt"))
                .setHeader("SlushyBody", bean(slushyTemplate))

                .setHeader("SlushyTemplateName", simple("badattachment/body.html"))
                .setHeader("SlushyBodyHtml", bean(slushyTemplate))

                .bean(emailService,
                        "send(" +
                                "${header.SlushyRecipient}, " +
                                "${header.SlushySender}, " +
                                "${header.SlushySubject}, " +
                                "${header.SlushyBody}, " +
                                "${header.SlushyBodyHtml})")
                .setHeader("SlushyComment",
                        () -> "Sent invalid attachment rejection notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")
                // move card to rejected
                .to("direct:Slushy.Reject.MoveToRejected")
        ;
    }

}
