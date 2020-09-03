package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Log
@ApplicationScoped
public class RestedFilter {

    @Inject Now now;

    boolean isRested(Card card, int requiredAgeHours) {
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
