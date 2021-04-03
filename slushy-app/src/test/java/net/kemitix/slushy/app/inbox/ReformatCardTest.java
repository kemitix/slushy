package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.Now;
import net.kemitix.trello.TrelloCard;
import net.kemitix.slushy.app.Submission;
import net.kemitix.trello.TrelloBoard;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ReformatCardTest
        implements WithAssertions {

    // collaborators
    InboxConfig inboxConfig = mock(InboxConfig.class);
    Now now = mock(Now.class);
    TrelloBoard trelloBoard = mock(TrelloBoard.class);

    // subjects
    ReformatCard reformatCard = new ReformatCard(inboxConfig, now, trelloBoard);

    // parameters
    TrelloCard card = mock(TrelloCard.class);
    Submission submission = mock(Submission.class);

    // values
    String storyTitle = UUID.randomUUID().toString();
    String authorByline = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        given(now.get()).willReturn(Instant.ofEpochSecond(1234567890));
        given(submission.getTitle()).willReturn(storyTitle);
        given(submission.getByline()).willReturn(authorByline);
        given(card.getDesc()).willReturn("");
    }

    @Test
    @DisplayName("requires SlushySubmission header")
    void requireSubmissionHeader() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        reformatCard.reformat(null, card));
    }

    @Test
    @DisplayName("requires SlushyCard header")
    void requireCardHeader() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        reformatCard.reformat(submission, null));
    }

    @Test
    void setsCardName() {
        //given
        String expectedName = storyTitle + " by " + authorByline;
        //when
        reformatCard.reformat(submission, card);
        //then
        verify(card).setName(expectedName);
        verify(trelloBoard).updateCard(card);
    }

    @Test
    void setsDueDate() {
        //given
        Date expectedDueDate = Date.from(Instant.ofEpochSecond(1234567890));
        //when
        reformatCard.reformat(submission, card);
        //then
        verify(card).setDue(expectedDueDate);
        verify(trelloBoard).updateCard(card);
    }

}