package net.kemitix.slushy.app.archiver;

import net.kemitix.slushy.app.OnException;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ArchiveCardRoute
        extends RouteBuilder {

    private final ArchiverProperties archiverProperties;
    private final ArchiveCard archiveCard;

    @Inject
    public ArchiveCardRoute(
            DynamicArchiverProperties archiverProperties,
            ArchiveCard archiveCard
    ) {
        this.archiverProperties = archiverProperties;
        this.archiveCard = archiveCard;
    }

    @Override
    public void configure() {
        OnException.retry(this, archiverProperties);

        from("direct:Slushy.ArchiveCard")
                .routeId("Slushy.ArchiveCard")
                .bean(archiveCard)
                .to("direct:Slushy.Status.Update")
        ;
    }
}
