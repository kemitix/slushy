package net.kemitix.slushy.app.trello;

import lombok.NonNull;
import net.kemitix.slushy.app.SlushyCard;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class GetListCards {

    @Inject TrelloBoard trelloBoard;

    List<SlushyCard> getCardsFromList(
            @NonNull @Header("SlushyListName") String listName
    ) {
        return trelloBoard.getListCards(listName);
    }

}
