package net.kemitix.slushy.trello;

import net.kemitix.trello.LoadCard;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Header;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Objects;

public class CardLoader {

    @Inject
    SlushyBoard slushyBoard;

    private LoadCard loadCard;

    @PostConstruct
    void init() {
        loadCard = new LoadCard(slushyBoard.getTrelloBoard());
    }

    public TrelloCard loadCard(@Header("SlushyCardId") String cardId) {
        return Objects.requireNonNull(slushyBoard.getCard(cardId), "Card Not Found");
    }

}
