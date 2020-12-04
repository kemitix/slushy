package net.kemitix.slushy.app.duedates;

import lombok.NonNull;
import net.kemitix.slushy.app.Now;
import net.kemitix.trello.TrelloCard;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@ApplicationScoped
public class SetDueInDays {

    @Inject TrelloBoard trelloBoard;
    @Inject Now now;

    void setDueDate(
            @NonNull @Header("SlushyCard") TrelloCard card,
            @NonNull @Header("SlushyDueInDays") Integer days
    ) {
        card.setDue(Date.from(now.get().plus(days, ChronoUnit.DAYS)));
        trelloBoard.updateCard(card);
    }

}
