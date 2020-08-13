package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import net.kemitix.slushy.spi.InboxConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

@ApplicationScoped
public class CardFormatter {

    @Inject Supplier<Instant> nowSupplier;
    @Inject InboxConfig inboxConfig;
    @Inject TrelloBoard trelloBoard;

    Submission reformat(Submission submission, Card card) {
        // Name
        card.setName(String.format(
                "%s by %s",
                submission.getTitle(),
                submission.getByline()));
        // Due Date
        card.setDue(new Date(nowSupplier.get()
                .plus(inboxConfig.getDueDays(), ChronoUnit.DAYS)
                .atZone(ZoneOffset.ofHours(0)).toEpochSecond() * 1000));
        // Save
        trelloBoard.updateCard(card);

        return submission;
    }

}
