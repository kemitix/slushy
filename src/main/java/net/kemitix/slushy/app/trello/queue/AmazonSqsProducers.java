package net.kemitix.slushy.app.trello.queue;

import software.amazon.awssdk.services.sqs.SqsClient;

import jakarta.enterprise.inject.Produces;

public class AmazonSqsProducers {

    @Produces
    SqsClient amazonSQS() {
        return SqsClient.create();
    }
}
