package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class MoveCard {

    @Inject
    SlushyBoard slushyBoard;

    @Handler
    public void move(
            @NonNull @Header(SlushyHeader.CARD) TrelloCard card,
            @NonNull @Header("SlushyTargetList") String targetList
    ) {
        card.setIdList(slushyBoard.getListId(targetList));
        card.setPos(getLastPos(targetList));
        slushyBoard.updateCard(card);
        log.info("Moved card to [" + targetList + "] - " + card.getName());
    }

    private int getLastPos(String listName) {
        return slushyBoard.getListCards(listName).stream()
                .map(Card::getPos)
                .max(Integer::compareTo)
                .orElse(1000) + 1;
    }

}
