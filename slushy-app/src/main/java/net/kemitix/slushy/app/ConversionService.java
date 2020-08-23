package net.kemitix.slushy.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.File;

@ApplicationScoped
public class ConversionService {

    @Inject
    Instance<AttachmentConverter> attachmentConverters;

    public Attachment convert(Attachment attachment) {
        return attachmentConverters.stream()
                .filter(converter -> converter.canHandle(attachment))
                .findFirst()
                .flatMap(converter -> converter.convert(attachment))
                .orElse(attachment);
    }

    public boolean canConvert(String filename) {
        Attachment attachment = new Attachment() {
            @Override
            public File getFileName() {
                return new File(filename);
            }
            @Override
            public Attachment download() {
                return null;
            }
        };
        return attachmentConverters.stream()
                .anyMatch(converter -> converter.canHandle(attachment));
    }

}
