package net.kemitix.slushy.app.trello.queue;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@ApplicationScoped
public class AmazonSqsProducers {

    @Produces
    @Named("amazonSQS")
    AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder.defaultClient();
    }
}
