package net.kemitix.slushy.app.inbox;

import lombok.NonNull;
import net.kemitix.slushy.app.CardBodyCleaner;
import net.kemitix.slushy.app.Now;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.trello.TrelloBoard;
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
            @NonNull @Header("SlushySubmission") Submission submission,
            @NonNull @Header("SlushyCard") SlushyCard card
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
