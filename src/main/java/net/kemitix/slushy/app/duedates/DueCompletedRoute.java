package net.kemitix.slushy.app.duedates;

import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DueCompletedRoute
        extends RouteBuilder {

    private final MarkCompleted markCompleted;

    @Inject
    public DueCompletedRoute(MarkCompleted markCompleted) {
        this.markCompleted = markCompleted;
    }

    @Override
    public void configure() {
        from("direct:Slushy.DueCompleted")
                .routeId("Slushy.DueCompleted")
                .bean(markCompleted)
        ;
    }

}
