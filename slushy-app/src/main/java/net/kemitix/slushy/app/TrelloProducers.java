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
    Supplier<Board> board(
            TrelloConfig trelloConfig,
            Trello trello
    ) {
        String userName = trelloConfig.getUserName();
        log.info("User: " + userName);
        String boardName = trelloConfig.getBoardName();
        log.info("Loading Board: " + boardName);
        return () ->
                trello
                        .getMemberBoards(userName)
                        .stream()
                        .filter(board -> board.getName().equals(boardName))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Board: " + boardName));
    }

    @Produces
    @Inbox
    String inboxName(InboxConfig inboxConfig) {
        return inboxConfig.getListName();
    }

    @Produces
    Supplier<List<TList>> boardLists(
            Supplier<Board> board,
            Trello trello
    ) {
        return () -> trello.getBoardLists(board.get().getId());
    }

    @Produces
    @Inbox
    Supplier<TList> inboxList(
            @Inbox String inboxName,
            Supplier<List<TList>> boardLists
    ) {
        return () -> {
            List<TList> lists = boardLists.get();
            log.info("Lists: " + lists.size());
            return lists
                    .stream()
                    .filter(list -> list.getName().equals(inboxName))
                    .peek(list -> log.info("List: " + list.getName()))
                    .findAny()
                    .orElseThrow(() -> new NotFoundException("List: " + inboxName));
        };
    }

    @Produces
    @Inbox
    Supplier<List<Card>> inboxCards(
            @Inbox Supplier<TList> inboxList,
            Trello trello
    ) {
        return () -> {
            List<Card> cards = trello.getListCards(inboxList.get().getId());
            log.info("Cards: " + cards.size());
            return cards;
        };
    }

}
