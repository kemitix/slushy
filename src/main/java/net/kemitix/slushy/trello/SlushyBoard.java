package net.kemitix.slushy.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Member;
import net.kemitix.trello.TrelloBoard;
import net.kemitix.trello.TrelloCard;
import net.kemitix.trello.TrelloConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class SlushyBoard {

    @Inject
    Trello trello;
    @Inject
    TrelloConfig trelloConfig;

    TrelloBoard trelloBoard;

    @PostConstruct
    void init() {
        trelloBoard = new TrelloBoard(trello, trelloConfig);
    }

    public TrelloBoard getTrelloBoard() {
        return trelloBoard;
    }

    public String getBoardId() {
        return trelloBoard.getBoardId();
    }

    public void updateCard(TrelloCard card) {
        trelloBoard.updateCard(card);
    }

    public String getListId(String listId) {
        return trelloBoard.getListId(listId);
    }

    public List<TrelloCard> getListCards(String listName) {
        return trelloBoard.getListCards(listName);
    }

    public TrelloCard addMemberToCard(TrelloCard card, Member member) {
        return trelloBoard.addMemberToCard(card, member);
    }

    public TrelloCard removeMemberFromCard(TrelloCard card, Member member) {
        return trelloBoard.removeMemberFromCard(card, member);
    }

    public Stream<String> getListNames() {
        return trelloBoard.getListNames();
    }

    public List<Attachment> getAttachments(Card card) {
        return trelloBoard.getAttachments(card);
    }

    public TrelloCard getCard(String cardId) {
        return trelloBoard.getCard(cardId);
    }
}
