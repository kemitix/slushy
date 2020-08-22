package net.kemitix.slushy.app.members;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Member;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class AddMember {

    @Inject TrelloBoard trelloBoard;
    @Inject Member member;

    Card addToCard(Card card) {
        log.info(String.format("Add %s to %s",
                member.getFullName(), card.getName()));
        return trelloBoard.addMemberToCard(card, member);
    }

}
