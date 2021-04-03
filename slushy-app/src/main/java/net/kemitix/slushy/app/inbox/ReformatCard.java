package net.kemitix.slushy.app.inbox;

import lombok.NonNull;
import net.kemitix.slushy.app.Now;
import net.kemitix.slushy.app.SlushyHeader;
import net.kemitix.trello.TrelloCard;
import net.kemitix.slushy.app.Submission;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@ApplicationScoped
public class ReformatCard {

    private final InboxConfig inboxConfig;
    private final Now now;
    private final TrelloBoard trelloBoard;

    @Inject
    public ReformatCard(
            InboxConfig inboxConfig,
            Now now,
            TrelloBoard trelloBoard
    ) {
        this.inboxConfig = inboxConfig;
        this.now = now;
        this.trelloBoard = trelloBoard;
    }

    @Handler
    Submission reformat(
            @NonNull @Header(SlushyHeader.SUBMISSION) Submission submission,
            @NonNull @Header(SlushyHeader.CARD) TrelloCard card
    ) {
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
