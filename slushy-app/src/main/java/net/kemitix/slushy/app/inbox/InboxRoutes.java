package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.ParseSubmission;
import net.kemitix.slushy.app.email.SendEmail;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.TemplatedRouteBuilder;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static net.kemitix.slushy.app.ListProcessRouteTemplate.PARAM_LIST_NAME;
import static net.kemitix.slushy.app.ListProcessRouteTemplate.PARAM_NAME;
import static net.kemitix.slushy.app.ListProcessRouteTemplate.PARAM_REQUIRED_AGE_HOURS;
import static net.kemitix.slushy.app.ListProcessRouteTemplate.PARAM_ROUTING_SLIP;
import static net.kemitix.slushy.app.ListProcessRouteTemplate.PARAM_SCAN_PERIOD;
import static net.kemitix.slushy.app.ListProcessRouteTemplate.ROUTE_LIST_PROCESS;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class InboxRoutes
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject InboxConfig inboxConfig;

    @Inject ParseSubmission parseSubmission;
    @Inject ReformatCard reformatCard;
    @Inject MoveCard moveCard;
    @Inject SendEmail sendEmail;
    @Inject AddComment addComment;
    @Inject CamelContext camelContext;

    @Override
    public void configure() {
        OnException.retry(this, inboxConfig);
      
        TemplatedRouteBuilder.builder(camelContext, ROUTE_LIST_PROCESS)
                .parameter(PARAM_NAME, "inbox")
                .parameter(PARAM_SCAN_PERIOD, inboxConfig.getScanPeriod())
                .parameter(PARAM_LIST_NAME, inboxConfig.getSourceList())
                .parameter(PARAM_REQUIRED_AGE_HOURS, inboxConfig.getRequiredAgeHours())
                .parameter(PARAM_ROUTING_SLIP, inboxConfig.getRoutingSlip())
                .add()
        ;

        from("direct:Slushy.CardToSubmission")
                .routeId("Slushy.CardToSubmission")
                .setHeader("SlushyCard", body())
                .bean(parseSubmission)
                .setHeader("SlushySubmission", body())
        ;

        from("direct:Slushy.ValidateAttachment")
                .routeId("Slushy.ValidateAttachment")
                .choice()
                .when(simple("${body.isValid}"))
                .otherwise()
                .to("direct:Slushy.InvalidAttachment")
                .process(exchange -> exchange.setRouteStop(true))
                .end()
        ;

        from("direct:Slushy.ReformatCard")
                .routeId("Slushy.ReformatCard")
                .bean(reformatCard)
        ;

        from("direct:Slushy.Inbox.MoveToTargetList")
                .routeId("Slushy.Inbox.MoveToTargetList")
                .setHeader("SlushyTargetList", inboxConfig::getTargetList)
                .bean(moveCard)
        ;

        from("direct:Slushy.Inbox.SendEmailConfirmation")
                .routeId("Slushy.Inbox.SendEmailConfirmation")
                .setHeader("SlushyEmailTemplate").constant("inbox")
                .to("direct:Slushy.SendEmail")
        ;
    }

}
