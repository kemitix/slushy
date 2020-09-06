package net.kemitix.slushy.app.members;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Member;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.SlushyCard;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class AddMember {

    @Inject TrelloBoard trelloBoard;
    @Inject Member member;

    @Handler
    SlushyCard addToCard(
            @NonNull @Header("SlushyCard") SlushyCard card) {
        log.info(String.format("Add %s to %s",
                member.getFullName(), card.getName()));
        if (card.getIdMembers().contains(member.getId())) {
            return card;
        }
        return trelloBoard.addMemberToCard(card, member);
    }

}
