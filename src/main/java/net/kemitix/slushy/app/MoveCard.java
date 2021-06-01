package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.trello.TrelloCard;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class MoveCard {

    @Inject TrelloBoard trelloBoard;

    @Handler
    public void move(
            @NonNull @Header(SlushyHeader.CARD) TrelloCard card,
            @NonNull @Header("SlushyTargetList") String targetList
    ) {
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
