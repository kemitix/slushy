package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Badges;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

public class SlushyCardTest
        implements WithAssertions {

    @Test
    void convertToSlushyCard() {
        //given
        Card card = getCard();
        Trello trello = null;
        //when
        SlushyCard slushyCard = SlushyCard.from(card, trello);
        //then
        assertThat((Card) slushyCard)
                .isEqualToIgnoringGivenFields(card, "dueComplete");

    }

    private Card getCard() {
        Card c = new Card();
        c.setId("id");
        c.setName("name");
        c.setIdList("idList");
        c.setDesc("desc");
        c.setUrl("url");
        c.setDue(new Date());
        c.setIdMembers(List.of("idMember"));
        c.setLabels(List.of(new Label()));
        c.setBadges(new Badges());
        c.setCheckItemStates(List.of(new Card.CardCheckItem()));
        c.setClosed(true);
        c.setDateLastActivity(new Date());
        c.setIdBoard("idBoard");
        c.setIdChecklists(List.of("idChecklists"));
        c.setIdMembersVoted(List.of("idMembersVoted"));
        c.setIdShort("idShort");
        c.setIdAttachmentCover("idAttachmentCover");
        c.setManualCoverAttachment(true);
        c.setPos(42);
        c.setShortLink("shortLink");
        c.setShortUrl("shortUrl");
        c.setSubscribed(true);
        return c;
    }
}