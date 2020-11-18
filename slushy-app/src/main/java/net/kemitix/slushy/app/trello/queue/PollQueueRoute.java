package net.kemitix.slushy.app.trello.queue;

import lombok.extern.java.Log;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@Log
@ApplicationScoped
public class PollQueueRoute
        extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        String queueName = Objects.requireNonNull(
                System.getenv("SLUSHY_QUEUE"));
        String region = Objects.requireNonNull(
                System.getenv("AWS_REGION")
        ).toUpperCase();

        fromF("aws-sqs://%s?region=%s&waitTimeSeconds=20&greedy=true",
                queueName, region)
                .routeId("Slushy.PollQueue")
                .process(exchange ->
                        log.info(exchange.getIn().getBody(String.class)))
                .setHeader("Slushy.WebHook.Source").jsonpath("$.queryParams.source")
                .process(exchange ->
                        log.info(String.format("Source: %s",
                                exchange.getIn().getHeader("Slushy.WebHook.Source"))))
        ;
    }
}
