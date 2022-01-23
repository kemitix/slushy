package net.kemitix.slushy.app;

import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class LoadList {

    @Inject
    SlushyBoard slushyBoard;

    public List<TrelloCard> loadList(@Header("ListName") String listName) {
        return slushyBoard.getListCards(listName);
    }

}
