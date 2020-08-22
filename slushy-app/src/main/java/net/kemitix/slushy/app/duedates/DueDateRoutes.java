package net.kemitix.slushy.app.duedates;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class DueDateRoutes
        extends RouteBuilder {

    @Inject DueDates dueDates;

    @Override
    public void configure() {
        from("direct:Slushy.DueIn30Days")
                .routeId("Slushy.DueIn30Days")
                .setHeader("Slushy.Due",
                        bean(dueDates, "nowPlusDays(30)"))
                .setHeader("Slushy.Inbox.Card",
                        bean(dueDates,
                                "setDueDate(${header[Slushy.Inbox.Card]}, ${header[Slushy.Due]})"))
        ;
    }

}
