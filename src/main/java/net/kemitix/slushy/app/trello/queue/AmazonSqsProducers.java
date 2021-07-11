package net.kemitix.slushy.app.trello.queue;

import io.quarkus.arc.Unremovable;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.enterprise.inject.Produces;

public class AmazonSqsProducers {

    @Produces
    @Unremovable
    SqsClient amazonSQS() {
        return SqsClient.create();
    }
}
