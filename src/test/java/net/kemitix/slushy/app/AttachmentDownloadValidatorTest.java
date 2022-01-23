package net.kemitix.slushy.app;

import net.kemitix.trello.LocalAttachment;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.api.WithAssumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

class AttachmentDownloadValidatorTest
        implements WithAssertions, WithAssumptions {

    private final ErrorHolder errorHolder = getErrorHolder();

    private ErrorHolder getErrorHolder() {
        final ErrorHolder errorHolder = new ErrorHolder();
        errorHolder.now = Instant::now;
        return errorHolder;
    }

    @Nested
    @DisplayName("Trello provides an attachment")
    class TrelloProvidesAttachment {

        private String fileName = "TrelloAttachment.docx";

        TrelloProvidesAttachment() throws IOException {
        }

        //given
        final InputStream source = getClass().getResourceAsStream(fileName);
        final File tempFile = File.createTempFile("trello", ".attachment");
        final long length = Files.copy(source, tempFile.toPath(), REPLACE_EXISTING);
        LocalAttachment attachment =
                new LocalAttachment(tempFile, new File(fileName), length);
        final AttachmentDownloadValidator sut = new AttachmentDownloadValidator();

        @Test
        void passesNormalFile() {
            //when
            final Optional<LocalAttachment> result = sut.apply(attachment);

            //then
            assertThat(result).contains(attachment);
        }
    }

    @Nested
    @DisplayName("Trello provides an normal html attachment")
    class TrelloProvidesNormalHtmlAttachment {

        private String fileName = "TrelloNormalHtmlFile.html";

        TrelloProvidesNormalHtmlAttachment() throws IOException {
        }

        //given
        final InputStream source = getClass().getResourceAsStream(fileName);
        final File tempFile = File.createTempFile("trello", ".attachment");
        final long length = Files.copy(source, tempFile.toPath(), REPLACE_EXISTING);
        LocalAttachment attachment =
                new LocalAttachment(tempFile, new File(fileName), length);
        final AttachmentDownloadValidator sut = new AttachmentDownloadValidator();

        @Test
        void passesNormalFile() {
            //when
            final Optional<LocalAttachment> result = sut.apply(attachment);

            //then
            assertThat(result).contains(attachment);
        }
    }

    @Nested
    @DisplayName("Trello prompted for login")
    class TrelloPromptsForLogin {

        private String fileName = "TrelloHtmlLoginForAttachment.html";

        TrelloPromptsForLogin() throws IOException {
        }

        //given
        final InputStream source = getClass().getResourceAsStream(fileName);
        final File tempFile = File.createTempFile("trello", ".attachment");
        final long length = Files.copy(source, tempFile.toPath(), REPLACE_EXISTING);
        LocalAttachment attachment =
                new LocalAttachment(tempFile, new File(fileName), length);
        final AttachmentDownloadValidator sut = new AttachmentDownloadValidator();

        @Test
        void canDetectAnHtmlLoginPage() {
            assertThatExceptionOfType(TrelloLoginPageException.class)
                    .isThrownBy(() -> sut.apply(attachment));
        }

        @Test
        void reportsError() {
            //given
            assumeThat(errorHolder.errors()).isEmpty();

            //when
            try {
                sut.apply(attachment);
            } catch (TrelloLoginPageException e) {
                //then
                assertThat(errorHolder.errors()).isNotEmpty();
            }
        }
    }

}

