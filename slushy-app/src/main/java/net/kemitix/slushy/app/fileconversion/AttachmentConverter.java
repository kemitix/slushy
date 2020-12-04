package net.kemitix.slushy.app.fileconversion;

import net.kemitix.trello.Attachment;
import net.kemitix.trello.LocalAttachment;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

public interface AttachmentConverter {

    boolean canHandle(Attachment attachment);

    Optional<LocalAttachment> convert(File sourceFile, File targetFile);

    Stream<String> canConvertFrom();
}
