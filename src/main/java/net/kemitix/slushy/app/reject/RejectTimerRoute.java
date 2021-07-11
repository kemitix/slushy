package net.kemitix.slushy.app.reject;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.trello.TrelloCard;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class RejectTimerRoute
        extends RouteBuilder {

    private final RejectProperties rejectProperties;
    private final TrelloBoard trelloBoard;
    private final IsRequiredAge isRequiredAge;

    @Inject
    public RejectTimerRoute(
            RejectProperties rejectProperties,
            TrelloBoard trelloBoard,
            IsRequiredAge isRequiredAge
    ) {
        this.rejectProperties = rejectProperties;
        this.trelloBoard = trelloBoard;
        this.isRequiredAge = isRequiredAge;
    }

    @Override
    public void configure() {
        OnException.retry(this, rejectProperties);

        fromF("timer:reject?period=%s", rejectProperties.scanPeriod())
                .routeId("Slushy.Reject")
                .setBody(exchange -> trelloBoard.getListCards(rejectProperties.sourceList()))
                .split(body())
                .convertBodyTo(TrelloCard.class)
                .setHeader("SlushyRequiredAge", rejectProperties::requiredAgeHours)
                .filter(bean(isRequiredAge))
                .setHeader("SlushyRoutingSlip", rejectProperties::routingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }

}
