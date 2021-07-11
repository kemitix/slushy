package net.kemitix.slushy.app.trello.queue;

import io.quarkus.arc.Unremovable;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class AmazonSqsProducers {

    @Produces
    @Unremovable
    SqsClient amazonSQS() {
        return SqsClient.create();
    }
}
