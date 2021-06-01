package net.kemitix.slushy.app;

import org.apache.camel.builder.RouteBuilder;

import java.io.IOException;

public class OnException {

    public static void retry(
            RouteBuilder routeBuilder,
            RetryConfig config
    ) {
        routeBuilder
                .onException(IOException.class)
                .maximumRedeliveries(config.getMaxRetries())
                .redeliveryDelay(config.getRetryDelay());
    }
}
