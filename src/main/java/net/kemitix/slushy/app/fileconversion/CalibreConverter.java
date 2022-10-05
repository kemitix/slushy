package net.kemitix.slushy.app.fileconversion;

import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.Submission;
import net.kemitix.trello.Attachment;
import net.kemitix.trello.LocalAttachment;
import org.zeroturnaround.exec.ProcessExecutor;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Log
@ApplicationScoped
public class CalibreConverter
        implements AttachmentConverter {

    private static final List<String> SUPPORTED_EXTENSIONS = List.of(
            "docx",
            "rtf",
            "odt",
            "html",
            "htm",
            "txt",
            "mobi",
            "azw"
    );

    @Inject
    DynamicConverterProperties converterProperties;

    @Override
    public boolean canHandle(Attachment attachment) {
        return canConvertFrom()
                .anyMatch(attachment.getOriginalFilename()
                        .getName()
                        .toLowerCase()::endsWith);
    }

    @SneakyThrows
    @Override
    public Optional<LocalAttachment> convert(
            File sourceFile,
            File targetFile,
            Submission submission
    ) {
        log.info("ebook-convert");
        log.info("Source File: " + sourceFile);
        log.info("Target File: " + targetFile);
        List<String> args = new ArrayList<>(List.of(
                "ebook-convert",
                sourceFile.toPath().toString(),
                targetFile.toPath().toString(),
                "--title", submission.getTitle(), /* <id> - <title> by <byline>.<source.ext>
                        Set the title. */
                "--title-sort", submission.getTitle(), /*
                        The version of the title to be used for sorting. */
                "--authors", submission.getByline(), /*
                        Set the authors. Multiple authors should be separated
                        by ampersands. */
                "--author-sort", submission.getByline(), /*
                        String to be used when sorting by author. */
                "--comments", "%s - %s\n<hr/>%s\n<hr/>\n%s".formatted(
                submission.getGenre().getValue(),
                submission.getWordLengthBand().getValue(),
                submission.getLogLine(),
                submission.getCoverLetter()), /*
                        Set the e-book description. */
                "--series", "Slushy", /*
                        Set the series this e-book belongs to. */
                "--series-index", submission.getId() /*
                        Set the index of the book in this series. */));
        if (converterProperties.smartenPunctuation()) {
            args.add("--smarten-punctuation");
        }
        args.add("--change-justification=%s".formatted(converterProperties.changeJustification()));
        if (converterProperties.insertBlankLine()) {
            args.add("--insert-blank-line");
        }
        if (converterProperties.removeParagraphSpacing()) {
            args.add("--remove-paragraph-spacing");
        }
        if (converterProperties.enableHeuristics()) {
            args.add("--enable-heuristics");
        }
        if (converterProperties.insertMetadata()) {
            args.add("--insert-metadata");
        }
        if (converterProperties.useAutoToc()) {
            args.add("--use-auto-toc");
        }
        log.info(String.join(" ", args));
        String output = new ProcessExecutor()
                .command(args)
                .readOutput(true)
                .execute()
                .outputUTF8();
        Arrays.stream(output.split(System.lineSeparator()))
                .forEach(log::info);
        if (targetFile.exists()) {
            return Optional.of(
                    new LocalAttachment(
                            targetFile, sourceFile, targetFile.length()));
        } else {
            throw new FileNotFoundException(targetFile.getAbsolutePath());
        }
    }

    @Override
    public Stream<String> canConvertFrom() {
        return SUPPORTED_EXTENSIONS.stream();
    }
}
