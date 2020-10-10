package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.trello.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ReaderIsFull {
    private final ReaderConfig readerConfig;
    private final TrelloBoard trelloBoard;

    @Inject
    public ReaderIsFull(ReaderConfig readerConfig, TrelloBoard trelloBoard) {
        this.readerConfig = readerConfig;
        this.trelloBoard = trelloBoard;
    }

    boolean test() {
        if (readerConfig.getMaxSize() == -1){
            return false;
        }
        int listSize = trelloBoard.getListCards(readerConfig.getTargetList()).size();
        return listSize >= readerConfig.getMaxSize();
    }
}
