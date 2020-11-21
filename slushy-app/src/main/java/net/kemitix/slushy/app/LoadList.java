package net.kemitix.slushy.app;

import net.kemitix.slushy.app.trello.TrelloBoard;
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

    public List<SlushyCard> loadList(@Header("ListName") String listName) {
        return trelloBoard.getListCards(listName);
    }

}
