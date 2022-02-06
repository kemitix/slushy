package net.kemitix.slushy.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.time.Instant;

@ApplicationScoped
public class TimeProducers {

    @Produces
    Now now() {
        return Instant::now;
    }

}
