package net.kemitix.slushy.app.trello;

import com.julienvey.trello.NotFoundException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.*;
import com.julienvey.trello.domain.Attachment;
import lombok.Getter;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.hold.HoldConfig;
import net.kemitix.slushy.spi.InboxConfig;
import net.kemitix.slushy.spi.RejectConfig;
import net.kemitix.slushy.spi.SlushyConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static net.kemitix.slushy.app.ListUtils.map;

@Log
@ApplicationScoped
public class TrelloBoard {

    @Inject private Trello trello;
    @Inject private SlushyConfig slushyConfig;
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
        Board board = board(slushyConfig, trello);
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


    public void updateCard(SlushyCard card) {
        trello.updateCard(card);
    }

    public List<SlushyCard> getInboxCards() {
        return getListCards(inbox.getId());
    }

    private List<SlushyCard> getListCards(String listId) {
        return trello.getListCards(listId).stream()
                .map(card -> SlushyCard.from(card, trello))
                .collect(Collectors.toList());
    }

    public List<SlushyCard> getSlushCards() {
        return getListCards(slush.getId());
    }

    public List<SlushyCard> getRejectCards() {
        return getListCards(reject.getId());
    }

    public List<SlushyCard> getHoldCards() {
        return getListCards(hold.getId());
    }

    public List<SlushyCard> getHeldCards() {
        return getListCards(held.getId());
    }

    public List<Attachment> getAttachments(Card card) {
        return trello.getCardAttachments(card.getId());
    }

    public Card addMemberToCard(Card card, Member member) {
        var members = trello.addMemberToCard(card.getId(), member.getId());
        card.setIdMembers(map(members, Member::getId));
        trello.updateCard(card);
        return card;
    }

    public Card removeMemberFromCard(Card card, Member member) {
        var members = trello.removeMemberFromCard(card.getId(), member.getId());
        card.setIdMembers(map(members, Member::getId));
        trello.updateCard(card);
        return card;
    }

}
