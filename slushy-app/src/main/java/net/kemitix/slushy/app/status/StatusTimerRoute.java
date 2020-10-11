package net.kemitix.slushy.app.status;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class StatusTimerRoute
        extends RouteBuilder {

    @Inject StatusConfig statusConfig;
    @Inject LogStatus logStatus;

    @Override
    public void configure() throws Exception {
        fromF("timer:status?period=%d", statusConfig.getLogPeriod())
                .routeId("Slushy.Status")

                .bean(logStatus)
        ;
    }
}
