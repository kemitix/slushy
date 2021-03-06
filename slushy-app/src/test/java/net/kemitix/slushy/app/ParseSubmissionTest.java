package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Card;
import net.kemitix.slushy.app.fileconversion.AttachmentConverter;
import net.kemitix.slushy.app.fileconversion.ConvertAttachment;
import net.kemitix.trello.TrelloBoard;
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

import javax.enterprise.inject.Instance;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ParseSubmissionTest
        implements WithAssertions {

    private final ParseSubmission parseSubmission = new ParseSubmission();

    private final Now now = () -> Instant.ofEpochSecond(123456789);
    private ValidFileTypes validFileTypes;

    @Mock TrelloBoard trelloBoard;
    @Mock ConvertAttachment convertAttachment;
    @Mock Instance<AttachmentConverter> attachmentConverters;
    @Mock AttachmentConverter attachmentConverter;

    @BeforeEach
    public void setUp() {
        parseSubmission.now = now;
        parseSubmission.trelloBoard = trelloBoard;
        parseSubmission.cardBodyCleaner = new CardBodyCleaner();
        validFileTypes = new ValidFileTypes(convertAttachment, attachmentConverters);
        parseSubmission.validFileTypes = validFileTypes;
        given(attachmentConverters.stream())
                .willReturn(Stream.of(attachmentConverter));
        given(attachmentConverter.canConvertFrom())
                .willReturn(Stream.of("rtf", "odt"));
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
            assertThat(parseSubmission.parse(card).getTitle())
                    .isEqualTo("TEST Story Title");
        }

        @Test
        @DisplayName("Parse Byline")
        public void parseByline() {
            assertThat(parseSubmission.parse(card).getByline())
                    .isEqualTo("TEST Author ByLine");
        }

        @Test
        @DisplayName("Parse Real Name")
        public void parseRealName() {
            assertThat(parseSubmission.parse(card).getRealName())
                    .isEqualTo("TEST Author Name");
        }

        @Test
        @DisplayName("Parse Email")
        public void parseEmail() {
            assertThat(parseSubmission.parse(card).getEmail())
                    .isEqualTo("my_email@example.com");
        }

        @Test
        @DisplayName("Parse Paypal")
        public void parsePaypal() {
            assertThat(parseSubmission.parse(card).getPaypal())
                    .isEqualTo("paypal@example.com");
        }

        @Test
        @DisplayName("Parse Word Length")
        public void parseWordLength() {
            assertThat(parseSubmission.parse(card).getWordLengthBand())
                    .isEqualTo(WordLengthBand.LENGTH_SHORT_SHORT);
        }

        @Test
        @DisplayName("Parse Cover Letter")
        public void parseCoverLetter() {
            assertThat(parseSubmission.parse(card).getCoverLetter())
                    .isEqualTo("TEST Cover Letter\n\nMore info.");
        }

        @Test
        @DisplayName("Parse Contract")
        public void parseContract() {
            assertThat(parseSubmission.parse(card).getContract())
                    .isEqualTo(Contract.ORIGINAL);
        }

        @Test
        @DisplayName("Parse Submitted Date")
        public void parseSubmittedDate() {
            assertThat(parseSubmission.parse(card).getDate())
                    .isEqualTo(Instant.ofEpochSecond(123456789));
        }

        @Test
        @DisplayName("Attachment")
        public void attachment() {
            assertThat(parseSubmission.parse(card).getDocument())
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
            assertThat(parseSubmission.parse(card).getDocument())
                    .isEqualTo(documentUrl);
        }

        @ParameterizedTest
        @DisplayName("Accepts convertible types")
        @ValueSource(strings = {"ODT", "RTF"})
        public void acceptsConvertibleTypes(String type) {
            documentUrl = "document." + type;
            given(trelloBoard.getAttachments(card))
                    .willReturn(List.of(new Attachment(documentUrl)));
            assertThat(parseSubmission.parse(card).getDocument())
                    .isEqualTo(documentUrl);
        }

        @Test
        @DisplayName("Reject invalid file type")
        public void rejectInvalidType() {
            documentUrl = "document.JPG";
            given(trelloBoard.getAttachments(card))
                    .willReturn(List.of(new Attachment(documentUrl)));
            assertThat(parseSubmission.parse(card).getDocument())
                    .isNull();
        }
    }

    @Nested
    @DisplayName("Legacy Valid Submission")
    // occasionally formsubmit.co will send an email with an old format
    public class LegacyValidSubmissionTests {

        Card card;
        URL validResource = this.getClass().getResource("legacy-valid-submission.txt");
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
            assertThat(parseSubmission.parse(card).getTitle())
                    .isEqualTo("TEST Story Title");
        }

        @Test
        @DisplayName("Parse Byline")
        public void parseByline() {
            assertThat(parseSubmission.parse(card).getByline())
                    .isEqualTo("TEST Author ByLine");
        }

        @Test
        @DisplayName("Parse Real Name")
        public void parseRealName() {
            assertThat(parseSubmission.parse(card).getRealName())
                    .isEqualTo("TEST Author Name");
        }

        @Test
        @DisplayName("Parse Email")
        public void parseEmail() {
            assertThat(parseSubmission.parse(card).getEmail())
                    .isEqualTo("my_email@example.com");
        }

        @Test
        @DisplayName("Parse Paypal")
        public void parsePaypal() {
            assertThat(parseSubmission.parse(card).getPaypal())
                    .isEqualTo("paypal@example.com");
        }

        @Test
        @DisplayName("Parse Word Length")
        public void parseWordLength() {
            assertThat(parseSubmission.parse(card).getWordLengthBand())
                    .isEqualTo(WordLengthBand.LENGTH_SHORT_SHORT);
        }

        @Test
        @DisplayName("Parse Cover Letter")
        public void parseCoverLetter() {
            assertThat(parseSubmission.parse(card).getCoverLetter())
                    .isEqualTo("TEST Cover Letter\n\nMore info.");
        }

        @Test
        @DisplayName("Parse Contract")
        public void parseContract() {
            assertThat(parseSubmission.parse(card).getContract())
                    .isEqualTo(Contract.ORIGINAL);
        }

        @Test
        @DisplayName("Parse Submitted Date")
        public void parseSubmittedDate() {
            assertThat(parseSubmission.parse(card).getDate())
                    .isEqualTo(Instant.ofEpochSecond(123456789));
        }

        @Test
        @DisplayName("Attachment")
        public void attachment() {
            assertThat(parseSubmission.parse(card).getDocument())
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
            assertThat(parseSubmission.parse(card).getDocument())
                    .isEqualTo(documentUrl);
        }

        @ParameterizedTest
        @DisplayName("Accepts convertible types")
        @ValueSource(strings = {"ODT", "RTF"})
        public void acceptsConvertibleTypes(String type) {
            documentUrl = "document." + type;
            given(trelloBoard.getAttachments(card))
                    .willReturn(List.of(new Attachment(documentUrl)));
            assertThat(parseSubmission.parse(card).getDocument())
                    .isEqualTo(documentUrl);
        }

        @Test
        @DisplayName("Reject invalid file type")
        public void rejectInvalidType() {
            documentUrl = "document.JPG";
            given(trelloBoard.getAttachments(card))
                    .willReturn(List.of(new Attachment(documentUrl)));
            assertThat(parseSubmission.parse(card).getDocument())
                    .isNull();
        }
    }

    @Nested
    @DisplayName("Invalid Submission")
    public class InvalidSubmissionTests {
        //TODO
    }
}