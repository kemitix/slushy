package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TrelloBoard {

    Trello trello;
    String inboxId;
    String slushId;

    public void updateCard(Card card) {
        trello.updateCard(card);
    }

    public List<Card> getInboxCards() {
        return trello.getListCards(inboxId);
    }

}
