package net.kemitix.slushy.app.trello;

import com.julienvey.trello.NotFoundException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.*;
import com.julienvey.trello.domain.Attachment;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.SlushyConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static net.kemitix.slushy.app.ListUtils.map;

@Log
@ApplicationScoped
public class TrelloBoard {

    private final Trello trello;
    private final SlushyConfig slushyConfig;

    private List<TList> lists;

    @Inject
    public TrelloBoard(
            Trello trello,
            SlushyConfig slushyConfig
    ) {
        this.trello = trello;
        this.slushyConfig = slushyConfig;
    }

    @PostConstruct
    void init () {
        lists = board().fetchLists();
    }

    private Board board() {
        String userName = slushyConfig.getUserName();
        log.info("User: " + userName);
        String boardName = slushyConfig.getBoardName();
        log.info("Loading Board: " + boardName);
        return trello
                .getMemberBoards(userName)
                .stream()
                .filter(board -> board.getName().equals(boardName))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Board: " + boardName));
    }

    private TList getList(String listName) {
        return lists
                .stream()
                .filter(list -> list.getName().equals(listName))
                .findAny()
                .orElseThrow(() -> new NotFoundException("List: " + listName));
    }


    public void updateCard(SlushyCard card) {
        trello.updateCard(card);
    }

    public List<SlushyCard> getListCards(String listName) {
        return trello.getListCards(getListId(listName)).stream()
                .map(card -> SlushyCard.from(card, trello))
                .collect(Collectors.toList());
    }

    public List<Attachment> getAttachments(Card card) {
        return trello.getCardAttachments(card.getId());
    }

    public SlushyCard addMemberToCard(SlushyCard card, Member member) {
        var members = trello.addMemberToCard(card.getId(), member.getId());
        card.setIdMembers(map(members, Member::getId));
        trello.updateCard(card);
        return card;
    }

    public SlushyCard removeMemberFromCard(SlushyCard card, Member member) {
        var members = trello.removeMemberFromCard(card.getId(), member.getId());
        card.setIdMembers(map(members, Member::getId));
        trello.updateCard(card);
        return card;
    }

    public String getListId(String listName) {
        return getList(listName).getId();
    }

    public String getBoardId() {
        return board().getId();
    }

}
