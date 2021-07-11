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

    private final ArchiverProperties archiverProperties;
    private final TrelloBoard trelloBoard;
    private final IsRequiredAge isRequiredAge;

    @Inject
    public ArchiveTimerRoute(
            ArchiverProperties archiverProperties,
            TrelloBoard trelloBoard,
            IsRequiredAge isRequiredAge
    ) {
        this.archiverProperties = archiverProperties;
        this.trelloBoard = trelloBoard;
        this.isRequiredAge = isRequiredAge;
    }

    @Override
    public void configure() {
        OnException.retry(this, archiverProperties);

        fromF("timer:archiver?period=%s", archiverProperties.scanPeriod())
                .routeId("Slushy.Archiver")
                .setBody(exchange -> trelloBoard.getListCards(archiverProperties.sourceList()))
                .split(body())

                .setHeader("SlushyRequiredAge", archiverProperties::requiredAgeHours)
                .filter().method(isRequiredAge)

                .setHeader("SlushyRoutingSlip", archiverProperties::routingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
