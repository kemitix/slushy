package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Comments {


    @Inject TrelloBoard trelloBoard;

    void add(Card card,String comment) {
        card.addComment(comment);
        trelloBoard.updateCard(card);
    }

}
