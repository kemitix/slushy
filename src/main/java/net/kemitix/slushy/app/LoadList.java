package net.kemitix.slushy.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Header;

import java.util.List;

@ApplicationScoped
public class LoadList {

    @Inject
    SlushyBoard slushyBoard;

    public List<TrelloCard> loadList(@Header("ListName") String listName) {
        return slushyBoard.getListCards(listName);
    }

}
