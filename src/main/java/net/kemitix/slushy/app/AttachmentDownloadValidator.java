package net.kemitix.slushy.app;

import net.kemitix.trello.LocalAttachment;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@ApplicationScoped
public class AttachmentDownloadValidator {

    private final ErrorHolder errorHolder;

    @Inject
    public AttachmentDownloadValidator(final ErrorHolder errorHolder) {
        this.errorHolder = errorHolder;
    }

    public Optional<LocalAttachment> apply(LocalAttachment localAttachment) {
        try {
            final Path pathToFile = localAttachment.getFilename().toPath();
            final byte[] fileContents = Files.readAllBytes(pathToFile);
            if (isHtmlFile(fileContents)
                    && isLogInToTrello(fileContents)) {
                errorHolder.add("Attachment from Trello is a Login Screen");
                return Optional.empty();
            }
        } catch (IOException e) {
            errorHolder.add("Unexpected error loading local file: " + e.getMessage());
            return Optional.empty();
        }
        return Optional.of(localAttachment);
    }

    private boolean isLogInToTrello(byte[] fileContents) {
        return new String(Arrays.copyOfRange(fileContents, 250, 350)).contains("Log in to Trello");
    }

    private boolean isHtmlFile(byte[] fileContents) {
        return "<!DOCTYPE".equals(new String(
                Arrays.copyOfRange(fileContents, 0, 9)));
    }

}
