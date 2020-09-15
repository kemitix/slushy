package net.kemitix.slushy.app.withdraw;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class WithdrawRoutes
        extends RouteBuilder {

    @Inject WithdrawConfig withdrawConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject IsRequiredAge isRequiredAge;

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

        from("direct:Slushy.Withdraw.SendEmail")
                .routeId("Slushy.Withdraw.SendEmail")
                .setHeader("SlushyEmailTemplate").constant("withdraw")
                .to("direct:Slushy.SendEmail")
        ;
    }

}
