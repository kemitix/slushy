package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
import lombok.extern.java.Log;
import net.kemitix.slushy.spi.RejectConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Log
@ApplicationScoped
public class RestedFilter {

    @Inject RejectConfig rejectConfig;
    @Inject Now now;

    boolean isRested(Card card) {
        Instant requiredAge = now.get()
                .minus(rejectConfig.getRequiredAgeHours(), ChronoUnit.HOURS);
        Instant dateLastActivity = card.getDateLastActivity().toInstant();
        boolean rested = dateLastActivity.isBefore(requiredAge);
        if (rested) {
            log.info(String.format("rested: %s", card.getName()));
        } else {
            log.info(String.format("not rested (%d hrs left): %s",
                    Duration.between(requiredAge, dateLastActivity).toHours(),
                    card.getName()));
        }
        return rested;
    }

}
