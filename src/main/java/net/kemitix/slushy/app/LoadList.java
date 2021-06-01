package net.kemitix.slushy.app;

import net.kemitix.trello.TrelloCard;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class LoadList {

    private final TrelloBoard trelloBoard;

    @Inject
    public LoadList(TrelloBoard trelloBoard) {
        this.trelloBoard = trelloBoard;
    }

    public List<TrelloCard> loadList(@Header("ListName") String listName) {
        return trelloBoard.getListCards(listName);
    }

}
