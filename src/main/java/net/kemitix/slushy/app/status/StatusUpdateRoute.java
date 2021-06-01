package net.kemitix.slushy.app.status;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class StatusUpdateRoute
        extends RouteBuilder {

    @Inject StatusConfig statusConfig;
    @Inject LogStatus logStatus;

    @Override
    public void configure() throws Exception {
        from("direct:Slushy.Status.Update")
                .routeId("Slushy.Status.Update")

                // Ignore additional messages sent within a second
                .idempotentConsumer().method(this, "tick")
                .skipDuplicate(true)
                .messageIdRepository(MemoryIdempotentRepository::new)

                .bean(logStatus)
        ;
    }

    public Instant tick() {
        return Instant.now();
    }
}