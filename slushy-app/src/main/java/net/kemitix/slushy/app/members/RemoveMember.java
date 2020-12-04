package net.kemitix.slushy.app.members;

import com.julienvey.trello.domain.Member;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.trello.TrelloCard;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class RemoveMember {

    @Inject TrelloBoard trelloBoard;
    @Inject Member member;

    @Handler
    TrelloCard removeFromCard(
            @NonNull @Header("SlushyCard") TrelloCard card
    ) {
        log.info(String.format("Remove %s from %s",
                member.getFullName(), card.getName()));
        if (card.getIdMembers().contains(member.getId())) {
            return trelloBoard.removeMemberFromCard(card, member);
        }
        return card;
    }

}
