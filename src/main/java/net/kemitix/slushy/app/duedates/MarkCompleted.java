package net.kemitix.slushy.app.duedates;

import lombok.NonNull;
import net.kemitix.slushy.app.SlushyHeader;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MarkCompleted {

    @Inject
    SlushyBoard slushyBoard;

    @Handler
    void completed(
            @NonNull @Header(SlushyHeader.CARD) TrelloCard card
    ) {
        card.setDueComplete(true);
        slushyBoard.updateCard(card);
    }

}
