package net.kemitix.slushy.app.fileconversion;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.Attachment;
import net.kemitix.slushy.app.LocalAttachment;
import org.bbottema.rtftohtml.RTF2HTMLConverter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Stream;

@Log
@ApplicationScoped
public class RtfToHtmlAttachmentConverter
        implements AttachmentConverter {

    @Inject RTF2HTMLConverter converter;
    @Inject HtmlCleaner htmlCleaner;
    @Inject RtfCleaner rtfCleaner;

    @Override
    public boolean canHandle(Attachment attachment) {
        return attachment.getOriginalFilename()
                .getName()
                .toLowerCase()
                .endsWith(".rtf");
    }

    @Override
    public Optional<LocalAttachment> convert(
            File sourceFile,
            File targetFile
    ) {
        try {
            String rtfString = Files.readString(sourceFile.toPath());
            String html = converter.rtf2html(
                    rtfCleaner.clean(
                            htmlCleaner.clean(rtfString)));
            Files.writeString(targetFile.toPath(), html);
            if (targetFile.exists()) {
                return Optional.of(
                        new LocalAttachment(
                                targetFile, sourceFile, targetFile.length()));
            } else {
                throw new FileNotFoundException(targetFile.getAbsolutePath());
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }


    @Override
    public Stream<String> canConvertFrom() {
        return Stream.of("rtf");
    }
}
