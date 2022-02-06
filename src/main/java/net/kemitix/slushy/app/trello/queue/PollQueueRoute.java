package net.kemitix.slushy.app.trello.queue;

import lombok.extern.java.Log;
import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;

import static net.kemitix.slushy.environment.EnvironmentUtil.requiredEnvironment;

@Log
@ApplicationScoped
public class PollQueueRoute
        extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        String queueName = requiredEnvironment("SLUSHY_QUEUE");
        String region = requiredEnvironment("AWS_REGION").toLowerCase();

        errorHandler(deadLetterChannel("direct:dropInvalidPollQueueMessage"));

        from("direct:dropInvalidPollQueueMessage")
                .log("Dropping Invalid Poll queue message: ${body}")
        ;

        fromF("aws2-sqs://%s?region=%s&waitTimeSeconds=20&greedy=true&useDefaultCredentialsProvider=true",
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
