package net.kemitix.slushy.app.hold;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class HoldTimerRoute
        extends RouteBuilder {

    @Inject DynamicHoldProperties holdProperties;
    @Inject TrelloBoard trelloBoard;
    @Inject IsRequiredAge isRequiredAge;

    @Override
    public void configure() {
        OnException.retry(this, holdProperties);

        fromF("timer:hold?period=%s", holdProperties.scanPeriod())
                .routeId("Slushy.Hold")
                .setBody(exchange -> trelloBoard.getListCards(holdProperties.sourceList()))
                .split(body())
                .setHeader("SlushyRequiredAge", holdProperties::requiredAgeHours)
                .filter(bean(isRequiredAge))
                .setHeader("SlushyRoutingSlip", holdProperties::routingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }

}
