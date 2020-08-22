package net.kemitix.slushy.app.duedates;

import com.julienvey.trello.domain.Card;
import net.kemitix.slushy.app.Now;
import net.kemitix.slushy.app.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@ApplicationScoped
public class DueDates {

    @Inject TrelloBoard trelloBoard;
    @Inject Now now;

    Card setDueDate(Card card, Instant due) {
        var date = Date.from(due);
        card.setDue(date);
        trelloBoard.updateCard(card);
        return card;
    }

    Instant nowPlusDays(int days) {
        return now.get().plus(days, ChronoUnit.DAYS);
    }

}
