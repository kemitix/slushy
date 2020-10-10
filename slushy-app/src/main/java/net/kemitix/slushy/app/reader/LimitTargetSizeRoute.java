package net.kemitix.slushy.app.reader;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Aborts the routing slip if the target list already contains the max limit
 * of cards.
 */
@ApplicationScoped
public class LimitTargetSizeRoute
        extends RouteBuilder {
    @Inject ReaderIsFull readerIsFull;
    @Override
    public void configure() {
        from("direct:Slushy.Reader.LimitTargetSize")
                .routeId("Slushy.Reader.LimitTargetSize")
                .choice()
                .when()
                .method(readerIsFull)
                .process(exchange -> exchange.setRouteStop(true))
                .end()
        ;
    }
}
