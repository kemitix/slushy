package net.kemitix.slushy.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ApplicationScoped
public class ErrorHolder {

    @Inject
    Now now;

    private final Integer maxCapacity = 10;
    private final Integer maxAgeSeconds = 1 * 24 * 60 * 60; // 1 day

    private final AtomicInteger errorId = new AtomicInteger();
    private final Map<Integer, Error> errors = new HashMap<>();

    public Error add(String message) {
        final int id = errorId.incrementAndGet();
        if (errors.size() == maxCapacity) {
            removeOldest();
        }
        final Error error = new Error(id, message);
        errors.put(id, error);
        return error;
    }

    private void removeOldest() {
        errors.values()
                .stream()
                .sorted(Comparator.comparing(e -> e.expires))
                .findFirst()
                .ifPresent(e -> acknowledge(e.id));
    }

    public List<Error> errors() {
        return errors.values()
                .stream()
                .filter(e -> e.expires.isAfter(now()))
                .collect(Collectors.toList());
    }

    private Instant now() {
        return now.get();
    }

    public void acknowledge(int id) {
        errors.remove(id);
    }

    public class Error {
        public final int id;
        public final Instant expires;
        public final String message;

        Error(
                final int id,
                final String message
        ) {
            this.id = id;
            this.expires = now().plus(maxAgeSeconds, ChronoUnit.SECONDS);
            this.message = message;
        }
    }

}
