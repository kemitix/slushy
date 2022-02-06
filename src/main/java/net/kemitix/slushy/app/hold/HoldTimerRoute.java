package net.kemitix.slushy.app.hold;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.trello.SlushyBoard;
import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class HoldTimerRoute
        extends RouteBuilder {

    @Inject
    DynamicHoldProperties holdProperties;
    @Inject
    SlushyBoard slushyBoard;
    @Inject
    IsRequiredAge isRequiredAge;

    @Override
    public void configure() {
        OnException.retry(this, holdProperties);

        fromF("timer:hold?period=%s", holdProperties.scanPeriod())
                .routeId("Slushy.Hold")
                .setBody(exchange -> slushyBoard.getListCards(holdProperties.sourceList()))
                .split(body())
                .setHeader("SlushyRequiredAge", holdProperties::requiredAgeHours)
                .filter(method(isRequiredAge))
                .setHeader("SlushyRoutingSlip", holdProperties::routingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }

}
