package net.kemitix.slushy.app;

import com.julienvey.trello.domain.TList;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.trello.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class CardMover {

    @Inject
    TrelloBoard trelloBoard;

    void move(SlushyCard card, TList destination) {
        card.setIdList(destination.getId());
        trelloBoard.updateCard(card);
        log.info("Moved card to [" + destination.getName() + "] - " + card.getName());
    }

}
