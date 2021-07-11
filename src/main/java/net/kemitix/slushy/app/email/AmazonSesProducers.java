package net.kemitix.slushy.app.email;

import software.amazon.awssdk.services.ses.SesClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class AmazonSesProducers {

    @Produces
    @ApplicationScoped
    SesClient amazonSimpleEmailService() {
        return SesClient.create();
    }

}
