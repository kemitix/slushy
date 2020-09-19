package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.AttachmentLoader;
import net.kemitix.slushy.app.MoveCard;
import net.kemitix.slushy.app.AddComment;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.email.SendEmailAttachment;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.TemplatedRouteBuilder;

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
public class ReaderRoutes
        extends RouteBuilder {

    @Inject SlushyConfig slushyConfig;
    @Inject ReaderConfig readerConfig;
    @Inject MoveCard moveCard;
    @Inject AttachmentLoader attachmentLoader;
    @Inject SendEmailAttachment sendEmailAttachment;
    @Inject AddComment addComment;
    @Inject CamelContext camelContext;

    @Override
    public void configure() {
        OnException.retry(this, readerConfig);

        TemplatedRouteBuilder.builder(camelContext, ROUTE_LIST_PROCESS)
                .parameter(PARAM_NAME, "reader")
                .parameter(PARAM_SCAN_PERIOD, readerConfig.getScanPeriod())
                .parameter(PARAM_LIST_NAME, readerConfig.getSourceList())
                .parameter(PARAM_REQUIRED_AGE_HOURS, readerConfig.getRequiredAgeHours())
                .parameter(PARAM_ROUTING_SLIP, readerConfig.getRoutingSlip())
                .add()
        ;

        from("direct:Slushy.LoadAttachment")
                .routeId("Slushy.LoadAttachment")
                .setHeader("SlushyAttachment",
                        bean(attachmentLoader, "load(${header.SlushyCard})"))
        ;

        from("direct:Slushy.SendToReader")
                .routeId("Slushy.SendToReader")

                .setHeader("SlushyRecipient", slushyConfig::getReader)
                .setHeader("SlushySender", slushyConfig::getSender)
                .setHeader("SlushySubject", simple("Reader: ${header.SlushyCard.name}"))
                .bean(sendEmailAttachment)

                .setHeader("SlushyComment")
                .constant("Sent attachment to reader")
                .bean(addComment)
        ;


        from("direct:Slushy.Reader.MoveToTargetList")
                .routeId("Slushy.Reader.MoveToTargetList")
                .setHeader("SlushyTargetList", readerConfig::getTargetList)
                .bean(moveCard)
        ;
    }

}
