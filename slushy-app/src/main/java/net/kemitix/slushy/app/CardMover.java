package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.trello.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class CardMover {

    @Inject TrelloBoard trelloBoard;

    void move(SlushyCard card, String targetList) {
        card.setIdList(trelloBoard.getListId(targetList));
        card.setPos(getLastPos(targetList));
        trelloBoard.updateCard(card);
        log.info("Moved card to [" + targetList + "] - " + card.getName());
    }

    private int getLastPos(String listName) {
        return trelloBoard.getListCards(listName).stream()
                .map(Card::getPos)
                .max(Integer::compareTo)
                .orElse(1000) + 1;
    }

}
