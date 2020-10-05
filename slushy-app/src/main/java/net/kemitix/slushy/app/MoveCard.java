package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.status.LogStatus;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class MoveCard {

    @Inject TrelloBoard trelloBoard;
    @Inject LogStatus logStatus;

    @Handler
    public void move(
            @NonNull @Header("SlushyCard") SlushyCard card,
            @NonNull @Header("SlushyTargetList") String targetList
    ) {
        card.setIdList(trelloBoard.getListId(targetList));
        card.setPos(getLastPos(targetList));
        trelloBoard.updateCard(card);
        log.info("Moved card to [" + targetList + "] - " + card.getName());
        logStatus.status();
    }

    private int getLastPos(String listName) {
        return trelloBoard.getListCards(listName).stream()
                .map(Card::getPos)
                .max(Integer::compareTo)
                .orElse(1000) + 1;
    }

}
