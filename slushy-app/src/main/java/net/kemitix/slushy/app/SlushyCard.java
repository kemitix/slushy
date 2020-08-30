package net.kemitix.slushy.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlushyCard extends Card {

    private boolean dueComplete;

    public static SlushyCard from(Card card, Trello trello) {
        SlushyCard s = new SlushyCard();
        s.setInternalTrello(trello);
        s.setId(card.getId());
        s.setName(card.getName());
        s.setIdList(card.getIdList());
        s.setDesc(card.getDesc());
        s.setUrl(card.getUrl());
        s.setDue(card.getDue());
        s.setIdMembers(card.getIdMembers());
        s.setLabels(card.getLabels());
        s.setBadges(card.getBadges());
        s.setCheckItemStates(card.getCheckItemStates());
        s.setClosed(card.isClosed());
        s.setDateLastActivity(card.getDateLastActivity());
        s.setIdBoard(card.getIdBoard());
        s.setIdChecklists(card.getIdChecklists());
        s.setIdMembersVoted(card.getIdMembersVoted());
        s.setIdShort(card.getIdShort());
        s.setIdAttachmentCover(card.getIdAttachmentCover());
        s.setManualCoverAttachment(card.isManualCoverAttachment());
        s.setPos(card.getPos());
        s.setShortLink(card.getShortLink());
        s.setShortUrl(card.getShortUrl());
        s.setSubscribed(card.isSubscribed());
        return s;
    }
}
