package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Card;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SubmissionParserTest
        implements WithAssertions {

    private final SubmissionParser submissionParser = new SubmissionParser();

    private final Now now = () -> Instant.ofEpochSecond(123456789);

    @Mock
    TrelloBoard trelloBoard;

    @BeforeEach
    public void setUp() {
        submissionParser.now = now;
        submissionParser.trelloBoard = trelloBoard;
    }

    @Nested
    @DisplayName("Valid Submission")
    public class ValidSubmissionTests {

        Card card;
        URL validResource = this.getClass().getResource("valid-submission.txt");
        String documentUrl = "document.docx";

        @BeforeEach
        public void setUp() throws URISyntaxException, IOException {
            card = new Card();
            String validCardDescription =
                    String.join("\n",
                            Files.readAllLines(Paths.get(validResource.toURI())));
            card.setDesc(validCardDescription);
            given(trelloBoard.getAttachments(card))
                    .willReturn(List.of(new Attachment(documentUrl)));
        }

        @Test
        @DisplayName("Parse Story Title")
        public void parseStoryTitle() {
            assertThat(submissionParser.parse(card).getTitle())
                    .isEqualTo("TEST Story Title");
        }

        @Test
        @DisplayName("Parse Byline")
        public void parseByline() {
            assertThat(submissionParser.parse(card).getByline())
                    .isEqualTo("TEST Author ByLine");
        }

        @Test
        @DisplayName("Parse Real Name")
        public void parseRealName() {
            assertThat(submissionParser.parse(card).getRealName())
                    .isEqualTo("TEST Author Name");
        }

        @Test
        @DisplayName("Parse Email")
        public void parseEmail() {
            assertThat(submissionParser.parse(card).getEmail())
                    .isEqualTo("email@example.com");
        }

        @Test
        @DisplayName("Parse Paypal")
        public void parsePaypal() {
            assertThat(submissionParser.parse(card).getPaypal())
                    .isEqualTo("paypal@example.com");
        }

        @Test
        @DisplayName("Parse Word Length")
        public void parseWordLength() {
            assertThat(submissionParser.parse(card).getWordLengthBand())
                    .isEqualTo(WordLengthBand.WORDS_3001_5000);
        }

        @Test
        @DisplayName("Parse Cover Letter")
        public void parseCoverLetter() {
            assertThat(submissionParser.parse(card).getCoverLetter())
                    .isEqualTo("TEST Cover Letter\n\nMore info.");
        }

        @Test
        @DisplayName("Parse Contract")
        public void parseContract() {
            assertThat(submissionParser.parse(card).getContract())
                    .isEqualTo(Contract.ORIGINAL);
        }

        @Test
        @DisplayName("Parse Submitted Date")
        public void parseSubmittedDate() {
            assertThat(submissionParser.parse(card).getDate())
                    .isEqualTo(Instant.ofEpochSecond(123456789));
        }

        @Test
        @DisplayName("Attachment")
        public void attachment() {
            assertThat(submissionParser.parse(card).getDocument())
                    .isEqualTo(documentUrl);
        }
    }

    @Nested
    @DisplayName("Invalid Submission")
    public class InvalidSubmissionTests {
        //TODO
    }
}