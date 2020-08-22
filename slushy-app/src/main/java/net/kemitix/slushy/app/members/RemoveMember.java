package net.kemitix.slushy.app.members;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Member;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class RemoveMember {

    @Inject TrelloBoard trelloBoard;
    @Inject Member member;

    Card removeFromCard(Card card) {
        log.info(String.format("Remove %s from %s",
                member.getFullName(), card.getName()));
        if (card.getIdMembers().contains(member.getId())) {
            return trelloBoard.removeMemberFromCard(card, member);
        }
        return card;
    }

}
