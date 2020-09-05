package net.kemitix.slushy.app.multisub;

import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.email.EmailService;
import net.kemitix.slushy.app.SlushyConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.apache.camel.builder.ValueBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class MultiSubmissionRoute
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject MultiSubmission multiSubmission;
    @Inject EmailService emailService;
    @Inject MultiSubmissionSubjectCreator subjectCreator;
    @Inject MultiSubmissionBodyCreator bodyCreator;
    @Inject Comments comments;

    @Override
    public void configure() {
        from("direct:Slushy.MultiSubMonitor")
                .routeId("Slushy.MultiSubMonitor")
                .bean(multiSubmission, "test(${header.SlushySubmission})")
                .choice()
                .when(body().isNotNull())
                .to("direct:Slushy.MultiSubDetected")
                .process(exchange -> exchange.setRouteStop(true))
                .end()
        ;

        from("direct:Slushy.MultiSubDetected")
                .routeId("Slushy.MultiSubDetected")
                .log("Submission rejected due to an existing submission")
                // send email to author
                .setHeader("SlushyRecipient", submissionEmail())
                .setHeader("SlushySender", slushyConfig::getSender)
                .setHeader("SlushySubject", subject())
                .setHeader("SlushyBody", bodyText())
                .setHeader("SlushyBodyHtml", bodyHtml())
                .bean(emailService,
                        "send(" +
                                "${header.SlushyRecipient}, " +
                                "${header.SlushySender}, " +
                                "${header.SlushySubject}, " +
                                "${header.SlushyBody}, " +
                                "${header.SlushyBodyHtml})")
                .setHeader("SlushyComment",
                        () -> "Sent multi-submission rejection notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")
                // move card to rejected
                .to("direct:Slushy.Reject.MoveToTargetList")
        ;
    }

    private SimpleBuilder submissionEmail() {
        return simple("${header.SlushySubmission.email}");
    }

    private ValueBuilder bodyHtml() {
        return bean(bodyCreator, "bodyHtml(${body})");
    }

    private ValueBuilder bodyText() {
        return bean(bodyCreator, "bodyText(${body})");
    }

    private ValueBuilder subject() {
        return bean(subjectCreator, "subject(${body})");
    }

}
