package net.kemitix.slushy.app;

import org.apache.camel.builder.RouteBuilder;

import java.io.IOException;

public class OnException {

    public static void retry(
            RouteBuilder routeBuilder,
            RetryProperties config
    ) {
        routeBuilder
                .onException(IOException.class)
                .maximumRedeliveries(config.maxRetries())
                .redeliveryDelay(config.retryDelay());
    }
}
