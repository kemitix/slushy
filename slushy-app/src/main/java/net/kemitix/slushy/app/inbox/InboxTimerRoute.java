package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.LoadList;
import net.kemitix.slushy.app.OnException;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Function;

@ApplicationScoped
public class InboxTimerRoute
        extends RouteBuilder {

    private final InboxConfig inboxConfig;
    private final LoadList loadList;

    @Inject
    public InboxTimerRoute(
            InboxConfig inboxConfig,
            LoadList loadList
    ) {
        this.inboxConfig = inboxConfig;
        this.loadList = loadList;
    }

    @Override
    public void configure() {
        OnException.retry(this, inboxConfig);

        fromF("timer:inbox?period=%s", inboxConfig.getScanPeriod())
                .routeId("Slushy.Inbox")

                .setHeader("ListName", inboxConfig::getSourceList)
                .setBody().method(loadList)
                .split(body())

                .to("direct:Slushy.Card.Inbox")
        ;
    }

}
