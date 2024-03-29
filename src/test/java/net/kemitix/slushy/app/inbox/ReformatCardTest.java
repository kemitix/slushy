package net.kemitix.slushy.app.inbox;

import lombok.SneakyThrows;
import net.kemitix.slushy.app.Genre;
import net.kemitix.slushy.app.Now;
import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.WordLengthBand;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ReformatCardTest
        implements WithAssertions {

    // collaborators
    DynamicInboxProperties inboxProperties = mock(DynamicInboxProperties.class);
    Now now = mock(Now.class);
    SlushyBoard slushyBoard = mock(SlushyBoard.class);

    // subjects
    ReformatCard reformatCard = new ReformatCard();

    // parameters
    TrelloCard card = mock(TrelloCard.class);
    Submission submission = mock(Submission.class);

    // values
    String cardId = UUID.randomUUID().toString();
    String storyTitle = UUID.randomUUID().toString();
    String authorByline = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        reformatCard.inboxProperties = inboxProperties;
        reformatCard.now = now;
        reformatCard.slushyBoard = slushyBoard;

        given(now.get()).willReturn(Instant.ofEpochSecond(1234567890));
        given(submission.getTitle()).willReturn(storyTitle);
        given(submission.getByline()).willReturn(authorByline);
        given(submission.getWordLengthBand()).willReturn(WordLengthBand.LENGTH_LONG_SHORT);
        given(submission.getGenre()).willReturn(Genre.Fantasy);
        given(card.getIdShort()).willReturn(cardId);
        given(card.getDesc()).willReturn("desc");
        given(submission.getLogLine()).willReturn("log line");
        given(submission.getCoverLetter()).willReturn("cover letter");
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
        String expectedName = cardId + " - " + storyTitle + " by " + authorByline;
        //when
        reformatCard.reformat(submission, card);
        //then
        verify(card).setName(expectedName);
        verify(slushyBoard).updateCard(card);
    }

    @Test
    void setsDueDate() {
        //given
        Date expectedDueDate = Date.from(Instant.ofEpochSecond(1234567890));
        //when
        reformatCard.reformat(submission, card);
        //then
        verify(card).setDue(expectedDueDate);
        verify(slushyBoard).updateCard(card);
    }

    @Nested
    @DisplayName("originalDescLines()")
    class OriginalDescLinesTests {

        @Test
        void canStripSummary() {
            //given
            var desc = readResource("card-desc-with-summary-v1.txt");
            given(card.getDesc()).willReturn(desc);
            //when
            var result = reformatCard.originalDescLines(card);
            //then
            assertThat(result).doesNotContain(ReformatCard.ORIGINAL_MARKER);
        }

        @Test
        void handleWhereNoSummary() {
            //given
            var desc = readResource("card-desc-original.txt");
            given(card.getDesc()).willReturn(desc);
            //when
            var result = reformatCard.originalDescLines(card);
            //then
            assertThat(result.collect(Collectors.joining("\n"))).isEqualTo(desc);
        }

    }

    @SneakyThrows
    private String readResource(String name) {
        return new String(
                Files.readAllBytes(
                        Paths.get(
                                ReformatCardTest.class
                                        .getResource(name)
                                        .getPath()
                        )));
    }
}
