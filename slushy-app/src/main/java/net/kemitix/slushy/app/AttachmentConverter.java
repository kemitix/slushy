package net.kemitix.slushy.app;

import java.util.Optional;

public interface AttachmentConverter {

    boolean canHandle(Attachment attachment);

    Optional<Attachment> convert(Attachment attachment);

}
