package net.kemitix.slushy.app.duedates;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DueDateRoutes
        extends RouteBuilder {

    @Inject SetDueInDays setDueInDays;
    @Inject MarkCompleted markCompleted;

    @Override
    public void configure() {
        from("direct:Slushy.DueIn30Days")
                .routeId("Slushy.DueIn30Days")
                .setHeader("SlushyDueInDays").simple("30")
                .bean(setDueInDays)
        ;

        from("direct:Slushy.DueIn60Days")
                .routeId("Slushy.DueIn60Days")
                .setHeader("SlushyDueInDays").simple("60")
                .bean(setDueInDays)
        ;

        from("direct:Slushy.DueCompleted")
                .routeId("Slushy.DueCompleted")
                .bean(markCompleted)
        ;
    }

}
