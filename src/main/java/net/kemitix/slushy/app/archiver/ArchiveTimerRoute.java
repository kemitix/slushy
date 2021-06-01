package net.kemitix.slushy.app.archiver;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ArchiveTimerRoute
        extends RouteBuilder {

    private final ArchiverConfig archiverConfig;
    private final TrelloBoard trelloBoard;
    private final IsRequiredAge isRequiredAge;

    @Inject
    public ArchiveTimerRoute(
            ArchiverConfig archiverConfig,
            TrelloBoard trelloBoard,
            IsRequiredAge isRequiredAge
    ) {
        this.archiverConfig = archiverConfig;
        this.trelloBoard = trelloBoard;
        this.isRequiredAge = isRequiredAge;
    }

    @Override
    public void configure() {
        OnException.retry(this, archiverConfig);

        fromF("timer:archiver?period=%s", archiverConfig.getScanPeriod())
                .routeId("Slushy.Archiver")
                .setBody(exchange -> trelloBoard.getListCards(archiverConfig.getSourceList()))
                .split(body())

                .setHeader("SlushyRequiredAge", archiverConfig::getRequiredAgeHours)
                .filter().method(isRequiredAge)

                .setHeader("SlushyRoutingSlip", archiverConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
