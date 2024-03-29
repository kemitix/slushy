package net.kemitix.slushy.app.reject;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RejectTimerRoute
        extends RouteBuilder {

    @Inject
    DynamicRejectProperties rejectProperties;
    @Inject
    SlushyBoard slushyBoard;
    @Inject
    IsRequiredAge isRequiredAge;

    @Override
    public void configure() {
        OnException.retry(this, rejectProperties);

        fromF("timer:reject?period=%s", rejectProperties.scanPeriod())
                .routeId("Slushy.Reject")
                .setBody(exchange -> slushyBoard.getListCards(rejectProperties.sourceList()))
                .split(body())
                .convertBodyTo(TrelloCard.class)
                .setHeader("SlushyRequiredAge", rejectProperties::requiredAgeHours)
                .filter(method(isRequiredAge))
                .setHeader("SlushyRoutingSlip", rejectProperties::routingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }

}
