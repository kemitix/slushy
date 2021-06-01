package net.kemitix.slushy.app.withdraw;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

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
