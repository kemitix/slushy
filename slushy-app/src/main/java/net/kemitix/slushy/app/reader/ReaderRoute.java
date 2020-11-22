package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.LoadList;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ReaderRoute
        extends RouteBuilder {

    @Inject LoadList loadList;
    @Inject ReaderConfig readerConfig;

    @Override
    public void configure() throws Exception {
        from("direct:Slushy.Reader")
                .routeId("Slushy.Reader")
                .setHeader("ListName").constant(readerConfig.getSourceList())
                .setBody().method(loadList)
                .split(body())
                .setHeader("SlushyRoutingSlip", readerConfig::getRoutingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
