package net.kemitix.slushy.app.reject;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class RejectRoutes
        extends RouteBuilder {

    @Inject RejectConfig rejectConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject MoveCard moveCard;
    @Inject IsRequiredAge isRequiredAge;

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
                .setHeader("SlushyEmailTemplate").constant("reject")
                .to("direct:Slushy.SendEmail")
        ;

        from("direct:Slushy.Reject.MoveToTargetList")
                .routeId("Slushy.Reject.MoveToTargetList")
                .setHeader("SlushyTargetList", rejectConfig::getTargetList)
                .bean(moveCard)
        ;

    }

}
