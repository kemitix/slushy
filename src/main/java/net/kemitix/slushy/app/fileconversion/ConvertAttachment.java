package net.kemitix.slushy.app.fileconversion;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.SlushyHeader;
import net.kemitix.slushy.app.Submission;
import net.kemitix.trello.AttachmentDirectory;
import net.kemitix.trello.LocalAttachment;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.io.File;
import java.util.Optional;

@Log
@ApplicationScoped
public class ConvertAttachment {

    @Inject
    Instance<AttachmentConverter> attachmentConverters;
    @Inject
    AttachmentDirectory attachmentDirectory;

    @Handler
    public LocalAttachment convert(
            @Header("SlushyAttachment") LocalAttachment attachment,
            @Header(SlushyHeader.SUBMISSION) Submission submission
    ) {
        return attachmentConverters.stream()
                .filter(converter -> converter.canHandle(attachment))
                .findFirst()
                .flatMap(converter -> convertAttachment(attachment, submission, converter))
                .orElse(attachment);
    }

    private Optional<LocalAttachment> convertAttachment(
            LocalAttachment attachment,
            Submission submission,
            AttachmentConverter converter
    ) {
        File sourceFile = attachment.getFilename();
        String name = sourceFile.getName();
        log.info("Converting from " + name);
        String epubName = name.substring(0, name.lastIndexOf(".")) + ".epub";
        File epubFile = attachmentDirectory.createFile(new File(epubName));
        log.info("Converting  to  " + epubFile.getAbsolutePath());
        return converter.convert(sourceFile, epubFile, submission);
    }

}
