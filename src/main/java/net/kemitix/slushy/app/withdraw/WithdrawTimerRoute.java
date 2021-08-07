package net.kemitix.slushy.app.withdraw;

import net.kemitix.slushy.app.LoadList;
import net.kemitix.slushy.app.OnException;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WithdrawTimerRoute
        extends RouteBuilder {

    private final WithdrawProperties withdrawProperties;
    private final LoadList loadList;

    @Inject
    public WithdrawTimerRoute(
            DynamicWithdrawProperties withdrawProperties,
            LoadList loadList
    ) {
        this.withdrawProperties = withdrawProperties;
        this.loadList = loadList;
    }

    @Override
    public void configure() {
        OnException.retry(this, withdrawProperties);

        fromF("timer:withdraw?period=%s", withdrawProperties.scanPeriod())
                .routeId("Slushy.Withdraw")

                .setHeader("ListName", withdrawProperties::sourceList)
                .setBody().method(loadList)
                .split(body())

                .to("direct:Slushy.Card.Withdrawn")
        ;
    }

}
