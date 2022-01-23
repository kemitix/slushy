package net.kemitix.slushy.app.members;

import com.julienvey.trello.domain.Member;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.SlushyHeader;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class RemoveMember {

    @Inject
    SlushyBoard slushyBoard;
    @Inject
    Member member;

    @Handler
    TrelloCard removeFromCard(
            @NonNull @Header(SlushyHeader.CARD) TrelloCard card
    ) {
        log.info(String.format("Remove %s from %s",
                member.getFullName(), card.getName()));
        if (card.getIdMembers().contains(member.getId())) {
            return slushyBoard.removeMemberFromCard(card, member);
        }
        return card;
    }

}
