package net.kemitix.slushy.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.Instant;
import java.util.function.Supplier;

@ApplicationScoped
public class TimeProducers {

    @Produces
    Supplier<Instant> nowSupplier() {
        return Instant::now;
    }

}
