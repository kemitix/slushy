package net.kemitix.slushy.app;

import net.kemitix.trello.LocalAttachment;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@ApplicationScoped
public class AttachmentDownloadValidator {

    public Optional<LocalAttachment> apply(LocalAttachment localAttachment) {
        try {
            final Path pathToFile = localAttachment.getFilename().toPath();
            System.out.println("pathToFile = " + pathToFile);

            final byte[] fileContents = Files.readAllBytes(pathToFile);
            final String header = new String(Arrays.copyOfRange(fileContents, 0, 9));
            System.out.println("header = " + header);

            if ("<!DOCTYPE".equals(header)) {
                // could be a trello login screen - or an html file
                // provide a wide range to search should their be any jitter in
                // the positioning of the text.
                final String stamp = new String(Arrays.copyOfRange(fileContents, 250, 350));
                System.out.println("stamp = " + stamp);
                if (stamp.contains("Log in to Trello")) {
                    // it's a trello login screen
                    //TODO report login screen
                    return Optional.empty();
                }
            }

        } catch (IOException e) {
            //TODO report error loading local file
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(localAttachment);
    }

}
