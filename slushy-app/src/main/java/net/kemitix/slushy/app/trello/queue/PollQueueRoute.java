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

        errorHandler(deadLetterChannel("direct:dropInvalidPollQueueMessage"));

        from("direct:dropInvalidPollQueueMessage")
                .log("Dropping Invalid Poll queue message: ${body}")
        ;

        fromF("aws-sqs://%s?region=%s&waitTimeSeconds=20&greedy=true",
                queueName, region)
                .routeId("Slushy.PollQueue")

                .setHeader("WebHookSource").jsonpath("queryParams.source")

                .choice()

                // Trello
                .when().simple("${header.WebHookSource} == 'trello'")
                .to("direct:Slushy.WebHook.Trello")

                // Unknown
                .otherwise()
                .log("SOURCE is UNKNOWN!")

                .end()
        ;
    }
}
