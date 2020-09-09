package net.kemitix.slushy.app.archiver;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class ArchiverRoutes
        extends RouteBuilder {

    @Inject ArchiverConfig archiverConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject IsRequiredAge isRequiredAge;
    @Inject ArchiveCard archiveCard;

    @Override
    public void configure() {
        OnException.retry(this, archiverConfig);

        fromF("timer:archiver?period=%s", archiverConfig.getScanPeriod())
                .routeId("Slushy.Archiver")
                .setBody(exchange -> trelloBoard.getListCards(archiverConfig.getSourceList()))
                .split(body())

                .setHeader("SlushyRequiredAge", archiverConfig::getRequiredAgeHours)
                .filter(bean(isRequiredAge))

                .setHeader("SlushyRoutingSlip", archiverConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;

        from("direct:Slushy.ArchiveCard")
                .routeId("Slushy.ArchiveCard")
                .bean(archiveCard)
        ;
    }
}
