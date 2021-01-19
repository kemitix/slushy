package net.kemitix.slushy.app.fileconversion;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import net.kemitix.slushy.app.Submission;
import net.kemitix.trello.Attachment;
import net.kemitix.trello.LocalAttachment;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@ApplicationScoped
public class MarkdownToHtmlConverter
        implements AttachmentConverter{

    private final MutableDataSet dataSet = new MutableDataSet();
    private final Parser parser = Parser.builder(dataSet).build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Inject HtmlCleaner htmlCleaner;

    @Override
    public boolean canHandle(Attachment attachment) {
        String filename =
                attachment.getOriginalFilename()
                        .getName()
                        .toLowerCase();
        return filename.endsWith(".md")
                || filename.endsWith(".markdown");
    }

    @Override
    public Optional<LocalAttachment> convert(File sourceFile, File targetFile, Submission submission) {
        try {
            String markdown = Files.readString(sourceFile.toPath(), UTF_8);
            String html = String.format(
                    "<html><title></title><body>%s</body></html>",
                    renderer.render(
                            parser.parse(
                                    htmlCleaner.clean(markdown))));
            Files.writeString(targetFile.toPath(), html);
            if (targetFile.exists()) {
                return Optional.of(
                        new LocalAttachment(
                                targetFile, sourceFile, targetFile.length()));
            } else {
                throw new FileNotFoundException(targetFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Stream<String> canConvertFrom() {
        return Stream.of("md");
    }
}
