package net.kemitix.slushy.app;

import net.kemitix.slushy.app.trello.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Comments {

    @Inject
    TrelloBoard trelloBoard;

    void add(SlushyCard card, String comment) {
        card.addComment(comment);
        trelloBoard.updateCard(card);
    }

}
