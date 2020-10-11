package net.kemitix.slushy.app.reject;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class RejectTimerRoute
        extends RouteBuilder {

    private final RejectConfig rejectConfig;
    private final TrelloBoard trelloBoard;
    private final IsRequiredAge isRequiredAge;

    @Inject
    public RejectTimerRoute(
            RejectConfig rejectConfig,
            TrelloBoard trelloBoard,
            IsRequiredAge isRequiredAge
    ) {
        this.rejectConfig = rejectConfig;
        this.trelloBoard = trelloBoard;
        this.isRequiredAge = isRequiredAge;
    }

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
    }

}
