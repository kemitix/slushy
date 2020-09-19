package net.kemitix.slushy.app.trello;

import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.SlushyCard;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Log
@ApplicationScoped
public class GetListCards {

    @Inject TrelloBoard trelloBoard;

    List<SlushyCard> getCardsFromList(
            @NonNull @Header("SlushyListName") String listName
    ) {
        List<SlushyCard> listCards = trelloBoard.getListCards(listName);
        log.info(String.format("Fetched %d cards from %s",
                listCards.size(), listName));
        return listCards;
    }

}
