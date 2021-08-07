package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.LoadList;
import net.kemitix.slushy.app.OnException;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InboxTimerRoute
        extends RouteBuilder {

    private final InboxProperties inboxProperties;
    private final LoadList loadList;

    @Inject
    public InboxTimerRoute(
            DynamicInboxProperties inboxProperties,
            LoadList loadList
    ) {
        this.inboxProperties = inboxProperties;
        this.loadList = loadList;
    }

    @Override
    public void configure() {
        OnException.retry(this, inboxProperties);

        fromF("timer:inbox?period=%s", inboxProperties.scanPeriod())
                .routeId("Slushy.Inbox")

                .setHeader("ListName", inboxProperties::sourceList)
                .setBody().method(loadList)
                .split(body())

                .to("direct:Slushy.Card.Inbox")
        ;
    }

}
