package net.kemitix.slushy.app.withdraw;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WithdrawRoute
        extends RouteBuilder {

    private final WithdrawConfig withdrawConfig;
    private final IsRequiredAge isRequiredAge;

    @Inject
    public WithdrawRoute(
            WithdrawConfig withdrawConfig,
            IsRequiredAge isRequiredAge
    ) {
        this.withdrawConfig = withdrawConfig;
        this.isRequiredAge = isRequiredAge;
    }

    @Override
    public void configure() throws Exception {
        OnException.retry(this, withdrawConfig);

        from("direct:Slushy.Card.Withdrawn")
                .routeId("Slushy.Card.Withdrawn")
                .log("Story being Withdrawn")

                .setHeader("SlushyRequiredAge", withdrawConfig::getRequiredAgeHours)
                .filter().method(isRequiredAge)

                .setHeader("SlushyRoutingSlip", withdrawConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
