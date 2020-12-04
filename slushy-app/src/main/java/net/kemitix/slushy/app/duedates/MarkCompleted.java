package net.kemitix.slushy.app.duedates;

import lombok.NonNull;
import net.kemitix.trello.TrelloCard;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MarkCompleted {

    @Inject TrelloBoard trelloBoard;

    @Handler
    void completed(
            @NonNull @Header("SlushyCard") TrelloCard card
    ) {
        card.setDueComplete(true);
        trelloBoard.updateCard(card);
    }

}
