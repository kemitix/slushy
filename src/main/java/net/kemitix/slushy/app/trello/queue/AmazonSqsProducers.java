package net.kemitix.slushy.app.trello.queue;

import software.amazon.awssdk.services.sqs.SqsClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class AmazonSqsProducers {

    @Produces
    SqsClient amazonSQS() {
        return SqsClient.create();
    }
}
