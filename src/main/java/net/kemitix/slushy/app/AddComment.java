package net.kemitix.slushy.app;

import lombok.NonNull;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AddComment {

    @Inject
    SlushyBoard slushyBoard;

    @Handler
    void add(
            @NonNull @Header(SlushyHeader.CARD) TrelloCard card,
            @NonNull @Header("SlushyComment") String comment
    ) {
        card.addComment(comment);
        slushyBoard.updateCard(card);
    }

}
