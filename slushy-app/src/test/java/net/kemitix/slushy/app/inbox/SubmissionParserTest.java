package net.kemitix.slushy.app.inbox;

import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Card;
import net.kemitix.slushy.app.*;
import net.kemitix.slushy.app.fileconversion.ConversionService;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private ValidFileTypes validFileTypes;

    @Mock
    TrelloBoard trelloBoard;

    @Mock
    ConversionService conversionService;

    @BeforeEach
    public void setUp() {
        submissionParser.now = now;
        submissionParser.trelloBoard = trelloBoard;
        validFileTypes = new ValidFileTypes(conversionService);
        submissionParser.validFileTypes = validFileTypes;
        given(conversionService.canConvertFrom())
                .willReturn(List.of("ODT", "RTF"));
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

        // Kindle Personal Documents Service:
        // https://www.amazon.co.uk/gp/help/customer/display.html?nodeId=200767340
        //        Kindle Format (.MOBI, .AZW)
        //        Microsoft Word (.DOC, .DOCX)
        //        HTML (.HTML, .HTM)
        //        Text (.TXT)
        // The following types claim to be supported by Kindle, but aren't
        //        RTF (.RTF)
        //        PDF (.PDF)
        // The following types are supported by Kindle, but we don't want them
        //        JPEG (.JPEG, .JPG)
        //        GIF (.GIF)
        //        PNG (.PNG)
        //        BMP (.BMP)
        @ParameterizedTest
        @DisplayName("Accepts Kindle supported types")
        @ValueSource(strings = {"MOBI", "AZW", "DOC", "DOCX", "HTML", "HTM", "TXT"})
        public void acceptsKindleTypes(String type) {
            documentUrl = "document." + type;
            given(trelloBoard.getAttachments(card))
                    .willReturn(List.of(new Attachment(documentUrl)));
            assertThat(submissionParser.parse(card).getDocument())
                    .isEqualTo(documentUrl);
        }

        @ParameterizedTest
        @DisplayName("Accepts convertible types")
        @ValueSource(strings = {"ODT", "RTF"})
        public void acceptsConvertibleTypes(String type) {
            documentUrl = "document." + type;
            given(trelloBoard.getAttachments(card))
                    .willReturn(List.of(new Attachment(documentUrl)));
            assertThat(submissionParser.parse(card).getDocument())
                    .isEqualTo(documentUrl);
        }

        @Test
        @DisplayName("Reject invalid file type")
        public void rejectInvalidType() {
            documentUrl = "document.JPG";
            given(trelloBoard.getAttachments(card))
                    .willReturn(List.of(new Attachment(documentUrl)));
            assertThat(submissionParser.parse(card).getDocument())
                    .isNull();
        }
    }

    @Nested
    @DisplayName("Invalid Submission")
    public class InvalidSubmissionTests {
        //TODO
    }
}