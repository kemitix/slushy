package net.kemitix.slushy.app.reader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.kemitix.slushy.app.MoveCard;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class ReaderMoveToTargetListRoute
        extends RouteBuilder {

    private final ReaderProperties readerProperties;
    private final MoveCard moveCard;
    private final ReaderIsFull readerIsFull;

    @Inject
    public ReaderMoveToTargetListRoute(
            DynamicReaderProperties readerProperties,
            MoveCard moveCard,
            ReaderIsFull readerIsFull
    ) {
        this.readerProperties = readerProperties;
        this.moveCard = moveCard;
        this.readerIsFull = readerIsFull;
    }

    @Override
    public void configure() {
        from("direct:Slushy.Reader.MoveToTargetList")
                .routeId("Slushy.Reader.MoveToTargetList")
                .setHeader("SlushyTargetList", readerProperties::targetList)
                .bean(moveCard)
                .bean(readerIsFull, "reset")
        ;
    }
}
