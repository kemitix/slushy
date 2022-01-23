package net.kemitix.slushy.app.fileconversion;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.SlushyHeader;
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
        String mobiName = name.substring(0, name.lastIndexOf(".")) + ".mobi";
        File mobiFile = attachmentDirectory.createFile(new File(mobiName));
        log.info("Converting  to  " + mobiFile.getAbsolutePath());
        return converter.convert(sourceFile, mobiFile, submission);
    }

}
