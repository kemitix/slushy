package net.kemitix.slushy.app.reject;

import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.email.SendEmail;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class RejectRoutes
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject RejectConfig rejectConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject MoveCard moveCard;
    @Inject SendEmail sendEmail;
    @Inject IsRequiredAge isRequiredAge;
    @Inject AddComment addComment;

    @Override
    public void configure() {
        OnException.retry(this, rejectConfig);

        fromF("timer:reject?period=%s", rejectConfig.getScanPeriod())
                .routeId("Slushy.Reject")
                .setBody(exchange -> trelloBoard.getListCards(rejectConfig.getSourceList()))
                .split(body())
                .convertBodyTo(SlushyCard.class)
                .setHeader("SlushyRequiredAge", rejectConfig::getRequiredAgeHours)
                .filter(bean(isRequiredAge))
                .setHeader("SlushyRoutingSlip", rejectConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;

        from("direct:Slushy.Reject.SendEmail")
                .routeId("Slushy.Reject.SendEmail")
                .setHeader("SlushyRecipient").simple("${header.SlushySubmission.email}")
                .setHeader("SlushySender", slushyConfig::getSender)
                .to("velocity:net/kemitix/slushy/app/reject/subject.txt")
                .setHeader("SlushySubject").body()
                .to("velocity:net/kemitix/slushy/app/reject/body.txt")
                .setHeader("SlushyBody").body()
                .to("velocity:net/kemitix/slushy/app/reject/body.html")
                .setHeader("SlushyBodyHtml").body()
                .bean(sendEmail)

                .setHeader("SlushyComment")
                .constant("Sent rejection notification to author")
                .bean(addComment)
        ;

        from("direct:Slushy.Reject.MoveToTargetList")
                .routeId("Slushy.Reject.MoveToTargetList")
                .setHeader("SlushyTargetList", rejectConfig::getTargetList)
                .bean(moveCard)
        ;

    }

}
