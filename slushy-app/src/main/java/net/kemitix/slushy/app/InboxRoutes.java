package net.kemitix.slushy.app;

import net.kemitix.slushy.spi.InboxConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InboxRoutes
        extends RouteBuilder {

    @Inject InboxConfig inboxConfig;

    @Override
    public void configure() throws Exception {
        fromF("timer:foo?period=%s", inboxConfig.getPeriod())
        .setBody(exchange -> "Tick!")
        .to("log:slushy-inbox");
    }
}
