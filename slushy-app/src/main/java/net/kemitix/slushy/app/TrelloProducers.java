package net.kemitix.slushy.app;

import com.julienvey.trello.NotFoundException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import lombok.extern.java.Log;
import net.kemitix.slushy.spi.InboxConfig;
import net.kemitix.slushy.spi.TrelloConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.function.Supplier;

@Log
@ApplicationScoped
public class TrelloProducers {

    @Produces
    Trello trello() {
        String key = System.getenv("TRELLO_KEY");
        String secret = System.getenv("TRELLO_SECRET");
        return new TrelloImpl(key, secret, new ApacheHttpClient());
    }

    @Produces
    Board board(
            TrelloConfig trelloConfig,
            Trello trello
    ) {
        String userName = trelloConfig.getUserName();
        log.info("User: " + userName);
        String boardName = trelloConfig.getBoardName();
        log.info("Loading Board: " + boardName);
        return trello
                .getMemberBoards(userName)
                .stream()
                .filter(board -> board.getName().equals(boardName))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Board: " + boardName));
    }

    @Produces
    @Inbox
    Supplier<List<Card>> inboxCards(
            Trello trello,
            Board board,
            InboxConfig inboxConfig
    ) {
        String listName = inboxConfig.getListName();
        return () -> {
            List<TList> lists = trello.getBoardLists(board.getId());
            return lists
                    .stream()
                    .filter(list -> list.getName().equals(listName))
                    .findAny()
                    .orElseThrow(() -> new NotFoundException("List: " + listName))
                    .getCards();
        };
    }

}
