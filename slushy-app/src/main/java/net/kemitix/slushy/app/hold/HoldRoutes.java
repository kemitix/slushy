package net.kemitix.slushy.app.hold;

import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.email.SendEmail;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class HoldRoutes
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject HoldConfig holdConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject MoveCard moveCard;
    @Inject SendEmail sendEmail;
    @Inject IsRequiredAge isRequiredAge;
    @Inject AddComment addComment;

    @Override
    public void configure() {
        OnException.retry(this, holdConfig);

        fromF("timer:hold?period=%s", holdConfig.getScanPeriod())
                .routeId("Slushy.Hold")
                .setBody(exchange -> trelloBoard.getListCards(holdConfig.getSourceList()))
                .split(body())
                .setHeader("SlushyRequiredAge", holdConfig::getRequiredAgeHours)
                .filter(bean(isRequiredAge))
                .setHeader("SlushyRoutingSlip", holdConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;

        from("direct:Slushy.Hold.SendEmail")
                .routeId("Slushy.Hold.SendEmail")
                .setHeader("SlushyRecipient").simple("${header.SlushySubmission.email}")
                .setHeader("SlushySender", slushyConfig::getSender)
                .to("velocity:net/kemitix/slushy/app/hold/subject.txt")
                .setHeader("SlushySubject").body()
                .to("velocity:net/kemitix/slushy/app/hold/body.txt")
                .setHeader("SlushyBody").body()
                .to("velocity:net/kemitix/slushy/app/hold/body.html")
                .setHeader("SlushyBodyHtml").body()
                .bean(sendEmail)

                .setHeader("SlushyComment").simple(
                        "Sent held notification to author")
                .bean(addComment)
        ;

        from("direct:Slushy.Hold.MoveToTargetList")
                .routeId("Slushy.Hold.MoveToTargetList")
                .setHeader("SlushyTargetList", holdConfig::getTargetList)
                .bean(moveCard)
        ;

    }

}
