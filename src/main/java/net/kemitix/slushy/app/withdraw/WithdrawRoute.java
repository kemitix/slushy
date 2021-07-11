package net.kemitix.slushy.app.withdraw;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WithdrawRoute
        extends RouteBuilder {

    private final WithdrawProperties withdrawProperties;
    private final IsRequiredAge isRequiredAge;

    @Inject
    public WithdrawRoute(
            WithdrawProperties withdrawProperties,
            IsRequiredAge isRequiredAge
    ) {
        this.withdrawProperties = withdrawProperties;
        this.isRequiredAge = isRequiredAge;
    }

    @Override
    public void configure() throws Exception {
        OnException.retry(this, withdrawProperties);

        from("direct:Slushy.Card.Withdrawn")
                .routeId("Slushy.Card.Withdrawn")
                .log("Story being Withdrawn")

                .setHeader("SlushyRequiredAge", withdrawProperties::requiredAgeHours)
                .filter().method(isRequiredAge)

                .setHeader("SlushyRoutingSlip", withdrawProperties::routingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
