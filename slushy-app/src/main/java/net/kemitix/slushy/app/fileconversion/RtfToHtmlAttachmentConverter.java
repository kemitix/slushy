package net.kemitix.slushy.app.fileconversion;

import lombok.extern.java.Log;
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

@Log
@ApplicationScoped
public class RtfToHtmlAttachmentConverter
        implements AttachmentConverter {

    private final Rtf2Html converter;
    private final RtfCleaner rtfCleaner;

    @Inject
    public RtfToHtmlAttachmentConverter(
            Rtf2Html converter,
            RtfCleaner rtfCleaner
    ) {
        this.converter = converter;
        this.rtfCleaner = rtfCleaner;
    }

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
            String html = doConvert(rtfString);
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

    public String doConvert(String rtfString) {
        return converter.convert(rtfCleaner.clean(rtfString));
    }

    @Override
    public Stream<String> canConvertFrom() {
        return Stream.of("rtf");
    }
}
