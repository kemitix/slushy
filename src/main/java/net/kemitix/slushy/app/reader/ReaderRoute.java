package net.kemitix.slushy.app.reader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.kemitix.slushy.app.LoadList;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class ReaderRoute
        extends RouteBuilder {

    public static final int TWENTY_SECONDS = 20000;
    @Inject
    LoadList loadList;
    @Inject
    DynamicReaderProperties readerProperties;

    @Override
    public void configure() throws Exception {
        from("direct:Slushy.Reader")
                .routeId("Slushy.Reader")

                .throttle(1).timePeriodMillis(TWENTY_SECONDS)

                .setHeader("ListName", readerProperties::sourceList)
                .setBody().method(loadList)
                .split(body())
                .setHeader("SlushyRoutingSlip", readerProperties::routingSlip)
                .routingSlip(header("SlushyRoutingSlip"))
        ;
    }
}
