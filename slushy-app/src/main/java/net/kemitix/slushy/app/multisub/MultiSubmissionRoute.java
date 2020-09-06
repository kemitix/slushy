package net.kemitix.slushy.app.multisub;

import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.email.EmailService;
import net.kemitix.slushy.app.SlushyConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class MultiSubmissionRoute
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject MultiSubmission multiSubmission;
    @Inject EmailService emailService;
    @Inject Comments comments;

    @Override
    public void configure() {
        from("direct:Slushy.MultiSubMonitor")
                .routeId("Slushy.MultiSubMonitor")
                .bean(multiSubmission, "test(${header.SlushySubmission})")
                .choice()
                .when(body().isNotNull())
                .setHeader("SlushyRejection").body()
                .to("direct:Slushy.MultiSubDetected")
                .process(exchange -> exchange.setRouteStop(true))
                .end()
        ;

        from("direct:Slushy.MultiSubDetected")
                .routeId("Slushy.MultiSubDetected")
                .log("Submission rejected due to an existing submission")
                // send email to author
                .setHeader("SlushyRecipient").simple("${header.SlushySubmission.email}")
                .setHeader("SlushySender", slushyConfig::getSender)

                .to("velocity:net/kemitix/slushy/app/multisub/subject.txt")
                .setHeader("SlushySubject").body()

                .to("velocity:net/kemitix/slushy/app/multisub/body.txt")
                .setHeader("SlushyBody").body()

                .to("velocity:net/kemitix/slushy/app/multisub/body.html")
                .setHeader("SlushyBodyHtml").body()

                .bean(emailService,
                        "send(" +
                                "${header.SlushyRecipient}, " +
                                "${header.SlushySender}, " +
                                "${header.SlushySubject}, " +
                                "${header.SlushyBody}, " +
                                "${header.SlushyBodyHtml})")

                .setHeader("SlushyComment").simple(
                        "Sent multi-submission rejection notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")

                // move card to rejected
                .to("direct:Slushy.Reject.MoveToTargetList")
        ;
    }

}
