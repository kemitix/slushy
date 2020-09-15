package net.kemitix.slushy.app.hold;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class HoldRoutes
        extends RouteBuilder {

    @Inject HoldConfig holdConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject MoveCard moveCard;
    @Inject IsRequiredAge isRequiredAge;

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
                .setHeader("SlushyEmailTemplate").constant("hold")
                .to("direct:Slushy.SendEmail")
        ;

        from("direct:Slushy.Hold.MoveToTargetList")
                .routeId("Slushy.Hold.MoveToTargetList")
                .setHeader("SlushyTargetList", holdConfig::getTargetList)
                .bean(moveCard)
        ;

    }

}
