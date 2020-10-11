package net.kemitix.slushy.app.archiver;

import net.kemitix.slushy.app.OnException;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ArchiveCardRoute
        extends RouteBuilder {

    private final ArchiverConfig archiverConfig;
    private final ArchiveCard archiveCard;

    @Inject
    public ArchiveCardRoute(
            ArchiverConfig archiverConfig,
            ArchiveCard archiveCard
    ) {
        this.archiverConfig = archiverConfig;
        this.archiveCard = archiveCard;
    }

    @Override
    public void configure() {
        OnException.retry(this, archiverConfig);

        from("direct:Slushy.ArchiveCard")
                .routeId("Slushy.ArchiveCard")
                .bean(archiveCard)
        ;
    }
}
