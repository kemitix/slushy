package net.kemitix.slushy.app.withdraw;

import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.RestedFilter;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.email.SendEmail;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class WithdrawRoutes
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject WithdrawConfig withdrawConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject RestedFilter restedFilter;
    @Inject SendEmail sendEmail;
    @Inject AddComment addComment;

    @Override
    public void configure() {
        fromF("timer:withdraw?period=%s", withdrawConfig.getScanPeriod())
                .routeId("Slushy.Withdraw")

                .setBody(exchange -> trelloBoard.getListCards(withdrawConfig.getSourceList()))
                .split(body())

                .setHeader("SlushyRequiredAge", withdrawConfig::getRequiredAgeHours)
                .filter(bean(restedFilter, "isRested(${body}, ${header.SlushyRequiredAge})"))

                .setHeader("SlushyRoutingSlip", withdrawConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;

        from("direct:Slushy.Withdraw.SendEmail")
                .routeId("Slushy.Withdraw.SendEmail")
                .setHeader("SlushyRecipient").simple("${header.SlushySubmission.email}")
                .setHeader("SlushySender", slushyConfig::getSender)
                .to("velocity:net/kemitix/slushy/app/withdraw/subject.txt")
                .setHeader("SlushySubject").body()
                .to("velocity:net/kemitix/slushy/app/withdraw/body.txt")
                .setHeader("SlushyBody").body()
                .to("velocity:net/kemitix/slushy/app/withdraw/body.html")
                .setHeader("SlushyBodyHtml").body()
                .bean(sendEmail)

                .setHeader("SlushyComment").simple(
                        "Sent withdrawn notification to author")
                .bean(addComment)
        ;
    }

}
