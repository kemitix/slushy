package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
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
        card.setPos(getLastPos(destination));
        trelloBoard.updateCard(card);
        log.info("Moved card to [" + destination.getName() + "] - " + card.getName());
    }

    private int getLastPos(TList destination) {
        return trelloBoard.getListCards(destination.getId()).stream()
                .map(Card::getPos)
                .max(Integer::compareTo)
                .orElse(1000) + 1;
    }

}
