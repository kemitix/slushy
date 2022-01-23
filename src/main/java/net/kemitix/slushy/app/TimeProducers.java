package net.kemitix.slushy.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.Instant;

@ApplicationScoped
public class TimeProducers {

    @Produces
    Now now() {
        return Instant::now;
    }

}
