package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.Now;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.trello.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@ApplicationScoped
public class CardFormatter {

    @Inject
    Now now;
    @Inject InboxConfig inboxConfig;
    @Inject
    TrelloBoard trelloBoard;

    Submission reformat(Submission submission, SlushyCard card) {
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