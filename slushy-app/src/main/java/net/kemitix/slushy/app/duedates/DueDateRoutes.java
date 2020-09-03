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
                .setHeader("SlushyCard",
                        bean(dueDates,
                                "setDueDate(${header.SlushyCard}, ${header[Slushy.Due]})"))
        ;

        from("direct:Slushy.DueIn60Days")
                .routeId("Slushy.DueIn60Days")
                .setHeader("Slushy.Due",
                        bean(dueDates, "nowPlusDays(60)"))
                .setHeader("SlushyCard",
                        bean(dueDates,
                                "setDueDate(${header.SlushyCard}, ${header[Slushy.Due]})"))
        ;

        from("direct:Slushy.DueCompleted")
                .routeId("Slushy.DueCompleted")
                .bean(dueDates, "completed(${header.SlushyCard})")
        ;
    }

}
