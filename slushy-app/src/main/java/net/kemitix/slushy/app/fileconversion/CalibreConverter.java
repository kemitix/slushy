package net.kemitix.slushy.app.fileconversion;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.Submission;
import net.kemitix.trello.Attachment;
import net.kemitix.trello.LocalAttachment;
import org.zeroturnaround.exec.ProcessExecutor;

import javax.enterprise.context.ApplicationScoped;
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
            "doc",
            "docx",
            "rtf",
            "odt"
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
                        "--title", sourceFile.getName(),
                        "--authors", submission.getByline(),
                        "--series", "Slushy",
                        "--series-index", submission.getId())
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
