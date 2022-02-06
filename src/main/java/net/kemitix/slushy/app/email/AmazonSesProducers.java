package net.kemitix.slushy.app.email;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import software.amazon.awssdk.services.ses.SesClient;

public class AmazonSesProducers {

    @Produces
    @ApplicationScoped
    SesClient amazonSimpleEmailService() {
        return SesClient.create();
    }

}
