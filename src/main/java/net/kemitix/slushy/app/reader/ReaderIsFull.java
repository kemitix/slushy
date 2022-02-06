package net.kemitix.slushy.app.reader;

import lombok.extern.java.Log;
import net.kemitix.slushy.trello.SlushyBoard;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Log
@ApplicationScoped
public class ReaderIsFull {

    @Inject
    DynamicReaderProperties readerProperties;
    @Inject
    SlushyBoard slushyBoard;

    private final AtomicInteger size = new AtomicInteger(0);
    private final AtomicReference<Instant> updated = new AtomicReference<>(Instant.MIN);
    private final TemporalAmount cachePeriod = Duration.of(1, ChronoUnit.MINUTES);

    boolean test() {
        int maxSize = readerProperties.maxSize();
        if (maxSize == -1){
            return false;
        }
        int listSize = getListSize();
        return listSize >= maxSize;
    }

    void reset() {
        updated.set(Instant.MIN);
    }

    private int getListSize() {
        if (updated.get().plus(cachePeriod).isBefore(Instant.now())) {
            size.set(slushyBoard.getListCards(readerProperties.targetList()).size());
            log.info(String.format("Fetched trello list size: %d", size.get()));
            updated.set(Instant.now());
        }
        return size.get();
    }

}
