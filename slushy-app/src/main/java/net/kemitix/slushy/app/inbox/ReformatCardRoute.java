package net.kemitix.slushy.app.inbox;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ReformatCardRoute
        extends RouteBuilder {
    private final ReformatCard reformatCard;

    @Inject
    public ReformatCardRoute(ReformatCard reformatCard) {
        this.reformatCard = reformatCard;
    }

    @Override
    public void configure() {
        from("direct:Slushy.ReformatCard")
                .routeId("Slushy.ReformatCard")
                .bean(reformatCard)
        ;
    }
}
