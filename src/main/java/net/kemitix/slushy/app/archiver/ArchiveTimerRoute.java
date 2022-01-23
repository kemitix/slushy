package net.kemitix.slushy.app.archiver;

import net.kemitix.slushy.app.IsRequiredAge;
import net.kemitix.slushy.app.OnException;
import net.kemitix.slushy.trello.SlushyBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ArchiveTimerRoute
        extends RouteBuilder {

    @Inject
    DynamicArchiverProperties archiverProperties;
    @Inject
    SlushyBoard slushyBoard;
    @Inject
    IsRequiredAge isRequiredAge;

    @Override
    public void configure() {
        OnException.retry(this, archiverProperties);

        fromF("timer:archiver?period=%s", archiverProperties.scanPeriod())
                .routeId("Slushy.Archiver")
                .setBody(exchange -> slushyBoard.getListCards(archiverProperties.sourceList()))
                .split(body())

                .setHeader("SlushyRequiredAge", archiverProperties::requiredAgeHours)
                .filter().method(isRequiredAge)

                .setHeader("SlushyRoutingSlip", archiverProperties::routingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
