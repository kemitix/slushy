package net.kemitix.slushy.app.inbox;

import lombok.SneakyThrows;
import net.kemitix.slushy.app.Genre;
import net.kemitix.slushy.app.Now;
import net.kemitix.slushy.app.WordLengthBand;
import net.kemitix.trello.TrelloCard;
import net.kemitix.slushy.app.Submission;
import net.kemitix.trello.TrelloBoard;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.New;
import javax.validation.valueextraction.UnwrapByDefault;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        given(submission.getWordLengthBand()).willReturn(WordLengthBand.LENGTH_LONG_SHORT);
        given(submission.getGenre()).willReturn(Genre.Fantasy);
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