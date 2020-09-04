package net.kemitix.slushy.app.fileconversion;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.Attachment;
import net.kemitix.slushy.app.AttachmentDirectory;
import net.kemitix.slushy.app.LocalAttachment;
import net.kemitix.slushy.app.fileconversion.AttachmentConverter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class ConversionService {

    private final Instance<AttachmentConverter> attachmentConverters;
    private final AttachmentDirectory attachmentDirectory;

    @Inject
    public ConversionService(
            Instance<AttachmentConverter> attachmentConverters,
            AttachmentDirectory attachmentDirectory
    ) {
        this.attachmentConverters = attachmentConverters;
        this.attachmentDirectory = attachmentDirectory;
    }

    public LocalAttachment convert(LocalAttachment attachment) {
        return attachmentConverters.stream()
                .filter(converter -> converter.canHandle(attachment))
                .findFirst()
                .flatMap(converter -> convertAttachment(attachment, converter))
                .orElse(attachment);
    }

    private Optional<LocalAttachment> convertAttachment(
            LocalAttachment attachment,
            AttachmentConverter converter
    ) {
        File sourceFile = attachment.getFileName();
        String name = sourceFile.getName();
        log.info("Converting from " + name);
        String htmlName = name.substring(0, name.length() - 3) + "html";
        File htmlFile = attachmentDirectory.createFile(new File(htmlName));
        log.info("Converting  to  " + htmlFile.getAbsolutePath());
        return converter.convert(sourceFile, htmlFile);
    }

    public boolean canConvert(String filename) {
        Attachment attachment = new Attachment() {
            @Override
            public File getFileName() {
                return new File(filename);
            }
            @Override
            public LocalAttachment download() {
                return null;
            }
        };
        return attachmentConverters.stream()
                .anyMatch(converter -> converter.canHandle(attachment));
    }

    public List<String> canConvertFrom() {
        return attachmentConverters.stream()
                .flatMap(AttachmentConverter::canConvertFrom)
                .collect(Collectors.toList());
    }

}
