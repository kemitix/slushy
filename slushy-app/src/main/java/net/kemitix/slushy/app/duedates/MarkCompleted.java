package net.kemitix.slushy.app.duedates;

import lombok.NonNull;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MarkCompleted {

    @Inject TrelloBoard trelloBoard;

    @Handler
    void completed(
            @NonNull @Header("SlushyCard") SlushyCard card
    ) {
        card.setDueComplete(true);
        trelloBoard.updateCard(card);
    }

}
