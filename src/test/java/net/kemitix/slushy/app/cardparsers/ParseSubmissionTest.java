package net.kemitix.slushy.app.cardparsers;

import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Card;
import net.kemitix.slushy.app.CardBodyCleaner;
import net.kemitix.slushy.app.Contract;
import net.kemitix.slushy.app.Genre;
import net.kemitix.slushy.app.Now;
import net.kemitix.slushy.app.ValidFileTypes;
import net.kemitix.slushy.app.WordLengthBand;
import net.kemitix.slushy.app.fileconversion.AttachmentConverter;
import net.kemitix.slushy.trello.SlushyBoard;
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

import jakarta.enterprise.inject.Instance;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ParseSubmissionTest
        implements WithAssertions {

    private final ParseSubmission parseSubmission = new ParseSubmission();

    private final Now now = () -> Instant.ofEpochSecond(123456789);

    @Mock
    SlushyBoard slushyBoard;
    @Mock
    Instance<AttachmentConverter> attachmentConverters;
    @Mock
    AttachmentConverter attachmentConverter;
    @Mock
    Instance<CardParser> cardParsers;

    @BeforeEach
    public void setUp() {
        parseSubmission.now = now;
        parseSubmission.slushyBoard = slushyBoard;
        parseSubmission.cardParsers = cardParsers;
        CardBodyCleaner cardBodyCleaner = new CardBodyCleaner();
        given(cardParsers.stream()).willReturn(Stream.of(
                cardParser(LegacyFormSubmitCoCardParser::new, cardBodyCleaner),
                cardParser(ManuallyForwardedFormSubmitCoCardParser::new, cardBodyCleaner),
                cardParser(FormSubmitCoCardParser::new, cardBodyCleaner),
                cardParser(FormSubmitIoCardParser::new, cardBodyCleaner)
        ));
        parseSubmission.validFileTypes = new ValidFileTypes();
        given(attachmentConverters.stream())
                .willReturn(Stream.of(attachmentConverter));
        given(attachmentConverter.canConvertFrom())
                .willReturn(Stream.of("rtf", "odt"));
    }

    private CardParser cardParser(
            Supplier<CardParser> supplier,
            CardBodyCleaner cardBodyCleaner
    ) {
        CardParser cardParser = supplier.get();
        cardParser.setCardBodyCleaner(cardBodyCleaner);
        return cardParser;
    }

    @Nested
    @DisplayName("formsubmit.co")
    class FormSubmitCoTests {

        private String getResourcePrefix() {
            return "formsubmit-co/";
        }

        @Nested
        @DisplayName("Valid Submission")
        public class ValidSubmissionTests {

            Card card;
            String documentUrl = "document.docx";

            @BeforeEach
            public void setUp() throws URISyntaxException, IOException {
                card = new Card();
                String validCardDescription =
                        String.join("\n",
                                Files.readAllLines(Paths.get(getValidResource().toURI())));
                card.setDesc(validCardDescription);
                given(slushyBoard.getAttachments(card))
                        .willReturn(List.of(new Attachment(documentUrl)));
            }

            protected URL getValidResource() {
                return this.getClass()
                        .getResource(getResourcePrefix() + "valid-submission.txt");
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
            @DisplayName("Parse LogLine")
            public void parseLogLine() {
                assertThat(parseSubmission.parse(card).getLogLine())
                        .isEqualTo("This is a short summary of what the story is about.");
            }

            @Test
            @DisplayName("Parse Genre")
            public void parseGenre() {
                assertThat(parseSubmission.parse(card).getGenre())
                        .isEqualTo(Genre.ScienceFiction);
            }

            @Test
            @DisplayName("Attachment")
            public void attachment() {
                assertThat(parseSubmission.parse(card).getDocument())
                        .isEqualTo(documentUrl);
            }

            // Kindle Personal Documents Service:
            // https://www.amazon.co.uk/gp/help/customer/display.html?nodeId=200767340
            //        EPUB (.EPUB)
            //        Microsoft Word (.DOC, .DOCX)
            //        HTML (.HTML, .HTM)
            //        Text (.TXT)
            // No longer supports the newest Kindle features and genrates a return email for each document sent to Kindle
            //        Kindle Format (.MOBI, .AZW)
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
            @ValueSource(strings = {"EPUB", "DOC", "DOCX", "HTML", "HTM", "TXT"})
            public void acceptsKindleTypes(String type) {
                documentUrl = "document." + type;
                given(slushyBoard.getAttachments(card))
                        .willReturn(List.of(new Attachment(documentUrl)));
                assertThat(parseSubmission.parse(card).getDocument())
                        .isEqualTo(documentUrl);
            }

            @ParameterizedTest
            @DisplayName("Accepts convertible types")
            @ValueSource(strings = {"ODT", "RTF", "MOBI"})
            public void acceptsConvertibleTypes(String type) {
                documentUrl = "document." + type;
                given(slushyBoard.getAttachments(card))
                        .willReturn(List.of(new Attachment(documentUrl)));
                assertThat(parseSubmission.parse(card).getDocument())
                        .isEqualTo(documentUrl);
            }

            @Test
            @DisplayName("Reject invalid file type")
            public void rejectInvalidType() {
                documentUrl = "document.JPG";
                given(slushyBoard.getAttachments(card))
                        .willReturn(List.of(new Attachment(documentUrl)));
                assertThat(parseSubmission.parse(card).getDocument())
                        .isNull();
            }
        }

        @Nested
        @DisplayName("Valid Manually Forwarded Submission")
        public class ValidManuallyForwardedSubmissionTests
                extends ValidSubmissionTests {

            @Override
            protected URL getValidResource() {
                return this.getClass()
                        .getResource(getResourcePrefix() + "valid-manual-forward.txt");
            }

        }

        @Nested
        @DisplayName("Legacy Valid Submission")
        // occasionally formsubmit.co will send an email with an old format
        public class LegacyValidSubmissionTests
                extends ValidSubmissionTests {

            @Override
            protected URL getValidResource() {
                return this.getClass()
                        .getResource(getResourcePrefix() + "legacy-valid-submission.txt");
            }

        }

        @Nested
        @DisplayName("Invalid Submission")
        public class InvalidSubmissionTests {
            //TODO
        }
    }

    @Nested
    @DisplayName("formsubmit.io")
    class FormSubmitIoTests {

        private String getResourcePrefix() {
            return "formsubmit-io/";
        }

        @Nested
        @DisplayName("Valid Submission")
        public class ValidSubmissionTests {

            Card card;
            String documentUrl = "document.docx";

            @BeforeEach
            public void setUp() throws URISyntaxException, IOException {
                card = new Card();
                String validCardDescription =
                        String.join("\n",
                                Files.readAllLines(Paths.get(getValidResource().toURI())));
                card.setDesc(validCardDescription);
                given(slushyBoard.getAttachments(card))
                        .willReturn(List.of(new Attachment(documentUrl)));
            }

            protected URL getValidResource() {
                return this.getClass()
                        .getResource(getResourcePrefix() + "valid-submission.txt");
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
            @DisplayName("Parse LogLine")
            public void parseLogLine() {
                assertThat(parseSubmission.parse(card).getLogLine())
                        .isEqualTo("This is a short summary of what the story is about.");
            }

            @Test
            @DisplayName("Parse Genre")
            public void parseGenre() {
                assertThat(parseSubmission.parse(card).getGenre())
                        .isEqualTo(Genre.ScienceFiction);
            }

            @Test
            @DisplayName("Attachment")
            public void attachment() {
                assertThat(parseSubmission.parse(card).getDocument())
                        .isEqualTo(documentUrl);
            }

            // Kindle Personal Documents Service:
            // https://www.amazon.co.uk/gp/help/customer/display.html?nodeId=200767340
            //        EPUB (.EPUB)
            //        Microsoft Word (.DOC, .DOCX)
            //        HTML (.HTML, .HTM)
            //        Text (.TXT)
            // No longer supports the newest Kindle features and genrates a return email for each document sent to Kindle
            //        Kindle Format (.MOBI, .AZW)
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
            @ValueSource(strings = {"EPUB", "DOC", "DOCX", "HTML", "HTM", "TXT"})
            public void acceptsKindleTypes(String type) {
                documentUrl = "document." + type;
                given(slushyBoard.getAttachments(card))
                        .willReturn(List.of(new Attachment(documentUrl)));
                assertThat(parseSubmission.parse(card).getDocument())
                        .isEqualTo(documentUrl);
            }

            @ParameterizedTest
            @DisplayName("Accepts convertible types")
            @ValueSource(strings = {"ODT", "RTF", "MOBI"})
            public void acceptsConvertibleTypes(String type) {
                documentUrl = "document." + type;
                given(slushyBoard.getAttachments(card))
                        .willReturn(List.of(new Attachment(documentUrl)));
                assertThat(parseSubmission.parse(card).getDocument())
                        .isEqualTo(documentUrl);
            }

            @Test
            @DisplayName("Reject invalid file type")
            public void rejectInvalidType() {
                documentUrl = "document.JPG";
                given(slushyBoard.getAttachments(card))
                        .willReturn(List.of(new Attachment(documentUrl)));
                assertThat(parseSubmission.parse(card).getDocument())
                        .isNull();
            }        }

    }
}
