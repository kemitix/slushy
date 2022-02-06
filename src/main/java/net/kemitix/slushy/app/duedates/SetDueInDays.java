package net.kemitix.slushy.app.duedates;

import lombok.NonNull;
import net.kemitix.slushy.app.Now;
import net.kemitix.slushy.app.SlushyHeader;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Header;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@ApplicationScoped
public class SetDueInDays {

    @Inject
    SlushyBoard slushyBoard;
    @Inject
    Now now;

    void setDueDate(
            @NonNull @Header(SlushyHeader.CARD) TrelloCard card,
            @NonNull @Header("SlushyDueInDays") Integer days
    ) {
        card.setDue(Date.from(now.get().plus(days, ChronoUnit.DAYS)));
        slushyBoard.updateCard(card);
    }

}
