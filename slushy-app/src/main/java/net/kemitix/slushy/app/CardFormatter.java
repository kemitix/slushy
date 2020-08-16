package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
import net.kemitix.slushy.spi.InboxConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@ApplicationScoped
public class CardFormatter {

    @Inject Now now;
    @Inject InboxConfig inboxConfig;
    @Inject TrelloBoard trelloBoard;

    Submission reformat(Submission submission, Card card) {
        // Name
        card.setName(String.format(
                "%s by %s",
                submission.getTitle(),
                submission.getByline()));
        // Due Date
        card.setDue(new Date(now.get()
                .plus(inboxConfig.getDueDays(), ChronoUnit.DAYS)
                .atZone(ZoneOffset.ofHours(0)).toEpochSecond() * 1000));
        // Save
        trelloBoard.updateCard(card);

        return submission;
    }

}
