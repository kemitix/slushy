package net.kemitix.slushy.app.fileconversion;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.Submission;
import net.kemitix.trello.AttachmentDirectory;
import net.kemitix.trello.LocalAttachment;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.File;
import java.util.Optional;

@Log
@ApplicationScoped
public class ConvertAttachment {

    private final Instance<AttachmentConverter> attachmentConverters;
    private final AttachmentDirectory attachmentDirectory;

    @Inject
    public ConvertAttachment(
            Instance<AttachmentConverter> attachmentConverters,
            AttachmentDirectory attachmentDirectory
    ) {
        this.attachmentConverters = attachmentConverters;
        this.attachmentDirectory = attachmentDirectory;
    }

    @Handler
    public LocalAttachment convert(
            @Header("SlushyAttachment") LocalAttachment attachment,
            @Header("SlushySubmission") Submission submission
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
        String htmlName = name.substring(0, name.lastIndexOf(".")) + ".html";
        File htmlFile = attachmentDirectory.createFile(new File(htmlName));
        log.info("Converting  to  " + htmlFile.getAbsolutePath());
        return converter.convert(sourceFile, htmlFile, submission);
    }

}
