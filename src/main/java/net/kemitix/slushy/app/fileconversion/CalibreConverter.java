package net.kemitix.slushy.app.fileconversion;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.Submission;
import net.kemitix.trello.Attachment;
import net.kemitix.trello.LocalAttachment;
import org.zeroturnaround.exec.ProcessExecutor;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileNotFoundException;
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
            "azw"
    );

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
        String output = new ProcessExecutor()
                .command("ebook-convert",
                        sourceFile.toPath().toString(),
                        targetFile.toPath().toString(),
                        "--smarten-punctuation", /*
                        Convert plain quotes, dashes and ellipsis to their
                        typographically correct equivalents. For details, see
                        https://daringfireball.net/projects/smartypants */
                        "--change-justification=justify", /*
                        Change text justification. A value of "left" converts
                        all justified text in the source to left aligned (i.e.
                        unjustified) text. A value of "justify" converts all
                        unjustified text to justified. A value of "original"
                        (the default) does not change justification in the
                        source file. Note that only some output formats
                        support justification. */
                        "--insert-blank-line", /*
                        Insert a blank line between paragraphs. Will not work
                        if the source file does not use paragraphs (<p> or
                        <div> tags). */
                        "--remove-paragraph-spacing", /*
                        Remove spacing between paragraphs. Also sets an indent
                        on paragraphs of 1.5em. Spacing removal will not work
                        if the source file does not use paragraphs (<p> or
                        <div> tags). */
                        "--enable-heuristics", /*
                        Enable heuristic processing. This option must be set
                        for any heuristic processing to take place. */
                        "--insert-metadata", /*
                        Insert the book metadata at the start of the book.
                        This is useful if your e-book reader does not support
                        displaying/searching metadata directly. */
                        "--use-auto-toc", /*
                        Normally, if the source file already has a Table of
                        Contents, it is used in preference to the auto-
                        generated one. With this option, the auto-generated
                        one is always used. */
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
                        Set the index of the book in this series. */
                        )
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
