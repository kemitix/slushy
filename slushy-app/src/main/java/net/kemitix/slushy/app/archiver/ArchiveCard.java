package net.kemitix.slushy.app.archiver;

import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.trello.TrelloCard;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class ArchiveCard {

    @Inject TrelloBoard trelloBoard;

    @Handler
    void archiveCard(
            @NonNull @Header("SlushyCard") TrelloCard card
    ) {
        card.setClosed(true);
        trelloBoard.updateCard(card);
        log.info("Archived: " + card.getName());
    }

}
