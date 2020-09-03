package net.kemitix.slushy.app.archiver;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.Comments;
import net.kemitix.slushy.app.RestedFilter;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class ArchiverRoutes
        extends RouteBuilder {

    @Inject ArchiverConfig archiverConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject RestedFilter restedFilter;
    @Inject Archiver archiver;

    @Override
    public void configure() {
        fromF("timer:archiver?period=%s", archiverConfig.getScanPeriod())
                .routeId("Slushy.Archiver")
                .setBody(exchange -> trelloBoard.getListCards(archiverConfig.getSourceList()))
                .split(body())
                .setHeader("Slushy.Age", archiverConfig::getRequiredAgeHours)
                .filter(bean(restedFilter, "isRested(${body}, ${header[Slushy.Age]})"))
                .bean(archiver)
        ;
    }
}
