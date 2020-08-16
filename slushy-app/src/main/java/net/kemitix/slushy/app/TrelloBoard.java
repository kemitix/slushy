package net.kemitix.slushy.app;

import com.julienvey.trello.NotFoundException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import lombok.Getter;
import lombok.extern.java.Log;
import net.kemitix.slushy.spi.HoldConfig;
import net.kemitix.slushy.spi.InboxConfig;
import net.kemitix.slushy.spi.RejectConfig;
import net.kemitix.slushy.spi.SlushyConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Log
@ApplicationScoped
public class TrelloBoard {

    @Inject private Trello trello;
    @Inject private SlushyConfig trelloConfig;
    @Inject private InboxConfig inboxConfig;
    @Inject private RejectConfig rejectConfig;
    @Inject private HoldConfig holdConfig;

    @Getter private TList inbox;
    @Getter private TList slush;
    @Getter private TList reject;
    @Getter private TList rejected;
    @Getter private TList hold;
    @Getter private TList held;

    @PostConstruct
    void init () {
        Board board = board(trelloConfig, trello);
        List<TList> lists = board.fetchLists();
        inbox = getList(inboxConfig.getListName(), lists);
        slush = getList(inboxConfig.getSlushName(), lists);
        reject = getList(rejectConfig.getRejectName(), lists);
        rejected = getList(rejectConfig.getRejectedName(), lists);
        hold = getList(holdConfig.getHoldName(), lists);
        held = getList(holdConfig.getHeldName(), lists);
    }

    private Board board(
            SlushyConfig trelloConfig,
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

    private TList getList(
            String listName,
            List<TList> lists
    ) {
        return lists
                .stream()
                .filter(list -> list.getName().equals(listName))
                .findAny()
                .orElseThrow(() -> new NotFoundException("List: " + listName));
    }


    public void updateCard(Card card) {
        trello.updateCard(card);
    }

    public List<Card> getInboxCards() {
        return trello.getListCards(inbox.getId());
    }

    public List<Card> getRejectCards() {
        return trello.getListCards(reject.getId());
    }

    public List<Card> getHoldCards() {
        return trello.getListCards(hold.getId());
    }

    public List<Attachment> getAttachments(Card card) {
        return trello.getCardAttachments(card.getId());
    }
}
