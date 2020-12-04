package net.kemitix.slushy.app.withdraw;

import net.kemitix.slushy.app.LoadList;
import net.kemitix.slushy.app.OnException;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class WithdrawTimerRoute
        extends RouteBuilder {

    private final WithdrawConfig withdrawConfig;
    private final LoadList loadList;

    @Inject
    public WithdrawTimerRoute(
            WithdrawConfig withdrawConfig,
            LoadList loadList
    ) {
        this.withdrawConfig = withdrawConfig;
        this.loadList = loadList;
    }

    @Override
    public void configure() {
        OnException.retry(this, withdrawConfig);

        fromF("timer:withdraw?period=%s", withdrawConfig.getScanPeriod())
                .routeId("Slushy.Withdraw")

                .setHeader("ListName").constant(withdrawConfig.getSourceList())
                .setBody().method(loadList)
                .split(body())

                .to("direct:Slushy.Card.Withdrawn")
        ;
    }

}
