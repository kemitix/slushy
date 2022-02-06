package net.kemitix.slushy.app.inbox;

import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReformatCardRoute
        extends RouteBuilder {

    @Inject
    private ReformatCard reformatCard;

    @Override
    public void configure() {
        from("direct:Slushy.ReformatCard")
                .routeId("Slushy.ReformatCard")
                .bean(reformatCard)
        ;
    }
}
