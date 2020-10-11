package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.MoveCard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ReaderMoveToTargetListRoute
        extends RouteBuilder {

    private final ReaderConfig readerConfig;
    private final MoveCard moveCard;

    @Inject
    public ReaderMoveToTargetListRoute(
            ReaderConfig readerConfig,
            MoveCard moveCard
    ) {
        this.readerConfig = readerConfig;
        this.moveCard = moveCard;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Reader.MoveToTargetList")
                .routeId("Slushy.Reader.MoveToTargetList")
                .setHeader("SlushyTargetList", readerConfig::getTargetList)
                .bean(moveCard)
        ;
    }
}
