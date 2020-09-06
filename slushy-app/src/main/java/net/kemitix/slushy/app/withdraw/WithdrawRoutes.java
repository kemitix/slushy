package net.kemitix.slushy.app.withdraw;

import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.RestedFilter;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.email.EmailService;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.apache.camel.builder.ValueBuilder;

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
    @Inject EmailService emailService;
    @Inject Comments comments;

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

                .bean(emailService, "send(" +
                        "${header.SlushyRecipient}, " +
                        "${header.SlushySender}, " +
                        "${header.SlushySubject}, " +
                        "${header.SlushyBody}, " +
                        "${header.SlushyBodyHtml}" +
                        ")"
                )

                .setHeader("SlushyComment",
                        () -> "Sent withdrawn notification to author")
                .bean(comments, "add(" +
                        "${header.SlushyCard}, " +
                        "${header.SlushyComment}" +
                        ")")
        ;
    }

}
