package net.kemitix.slushy.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Log
@ApplicationScoped
public class IsRequiredAge {

    @Inject
    Now now;

    @Handler
    boolean isRested(
            @NonNull @Body TrelloCard card,
            @NonNull @Header("SlushyRequiredAge") Integer requiredAgeHours
    ) {
        Instant requiredAge = now.get()
                .minus(requiredAgeHours, ChronoUnit.HOURS);
        Instant dateLastActivity = card.getDateLastActivity().toInstant();
        boolean rested = dateLastActivity.isBefore(requiredAge);
        if (!rested) {
            log.info(String.format("not rested (%d hrs left): %s",
                    Duration.between(requiredAge, dateLastActivity).toHours(),
                    card.getName()));
        }
        return rested;
    }

}
