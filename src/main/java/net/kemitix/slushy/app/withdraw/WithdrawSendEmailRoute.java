package net.kemitix.slushy.app.withdraw;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class WithdrawSendEmailRoute
        extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:Slushy.Withdraw.SendEmail")
                .routeId("Slushy.Withdraw.SendEmail")
                .setHeader("SlushyEmailTemplate").constant("withdraw")
                .to("direct:Slushy.SendEmail")
        ;
    }
}
