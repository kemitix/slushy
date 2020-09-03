package net.kemitix.slushy.app.archiver;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.trello.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class Archiver {

    @Inject TrelloBoard trelloBoard;

    void archiveCard(SlushyCard card) {
        card.setClosed(true);
        trelloBoard.updateCard(card);
        log.info("Archived: " + card.getName());
    }

}
