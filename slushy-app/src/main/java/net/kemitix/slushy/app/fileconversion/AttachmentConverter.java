package net.kemitix.slushy.app.fileconversion;

import net.kemitix.slushy.app.Attachment;

import java.util.Optional;
import java.util.stream.Stream;

public interface AttachmentConverter {

    boolean canHandle(Attachment attachment);

    Optional<Attachment> convert(Attachment attachment);

    Stream<String> canConvertFrom();
}
