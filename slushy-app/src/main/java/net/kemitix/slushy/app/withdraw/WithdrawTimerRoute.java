package net.kemitix.slushy.app.withdraw;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class WithdrawTimerRoute
        extends RouteBuilder {

    private final WithdrawConfig withdrawConfig;
    private final TrelloBoard trelloBoard;
    private final IsRequiredAge isRequiredAge;

    @Inject
    public WithdrawTimerRoute(
            WithdrawConfig withdrawConfig,
            TrelloBoard trelloBoard,
            IsRequiredAge isRequiredAge
    ) {
        this.withdrawConfig = withdrawConfig;
        this.trelloBoard = trelloBoard;
        this.isRequiredAge = isRequiredAge;
    }

    @Override
    public void configure() {
        OnException.retry(this, withdrawConfig);

        fromF("timer:withdraw?period=%s", withdrawConfig.getScanPeriod())
                .routeId("Slushy.Withdraw")

                .setBody(exchange -> trelloBoard.getListCards(withdrawConfig.getSourceList()))
                .split(body())

                .setHeader("SlushyRequiredAge", withdrawConfig::getRequiredAgeHours)
                .filter(bean(isRequiredAge))

                .setHeader("SlushyRoutingSlip", withdrawConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }

}
