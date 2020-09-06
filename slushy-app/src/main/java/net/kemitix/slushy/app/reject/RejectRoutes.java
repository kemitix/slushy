package net.kemitix.slushy.app.reject;

import net.kemitix.slushy.app.CardMover;
import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.RestedFilter;
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
    @Inject CardMover cardMover;
    @Inject SendEmail sendEmail;
    @Inject RestedFilter restedFilter;
    @Inject Comments comments;

    @Override
    public void configure() {
        fromF("timer:reject?period=%s", rejectConfig.getScanPeriod())
                .routeId("Slushy.Reject")
                .setBody(exchange -> trelloBoard.getListCards(rejectConfig.getSourceList()))
                .split(body())
                .convertBodyTo(SlushyCard.class)
                .setHeader("SlushyRequiredAge", rejectConfig::getRequiredAgeHours)
                .filter(bean(restedFilter, "isRested(${body}, ${header.SlushyRequiredAge})"))
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

                .setHeader("SlushyComment",
                        () -> "Sent rejection notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")
        ;

        from("direct:Slushy.Reject.MoveToTargetList")
                .routeId("Slushy.Reject.MoveToTargetList")
                .setHeader("SlushyTargetList", rejectConfig::getTargetList)
                .bean(cardMover, "move(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyTargetList}" +
                        ")")
        ;

    }

}
