package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TrelloAttachment implements Attachment {

    private static final Logger LOG =
            Logger.getLogger(
                    TrelloAttachment.class.getName());

    private static final String[] EXTENSIONS = new String[]{"doc", "docx", "odt"};
    private static final AtomicInteger SEQUENCE_COUNTER = new AtomicInteger();
    private final com.julienvey.trello.domain.Attachment attachment;
    private final Card card;
    private final AttachmentDirectory attachmentDirectory;
    private final int sequence;

    private TrelloAttachment(
            com.julienvey.trello.domain.Attachment attachment,
            Card card,
            AttachmentDirectory attachmentDirectory
    ) {
        this.attachment = attachment;
        this.card = card;
        this.attachmentDirectory = attachmentDirectory;
        this.sequence = SEQUENCE_COUNTER.incrementAndGet();
    }

    public static Attachment create(
            com.julienvey.trello.domain.Attachment attachment,
            Card card,
            AttachmentDirectory dir
    ) {
        return new TrelloAttachment(attachment, card, dir);
    }

    @Override
    public File getFileName() {
        return new File(String.format("%2d - %s.%s",
                sequence, card.getName(), extension()));
    }

    private String extension() {
        URI uri = URI.create(attachment.getUrl());
        String path = uri.getPath();
        for (String ex : EXTENSIONS) {
            if (path.endsWith("." + ex)) {
                return ex;
            }
        }
        return "";
    }

    @Override
    public Attachment download() {
        try (var source = Channels.newChannel(getUrl().openStream());){
            var file = attachmentDirectory.createFile(getFileName());
            LOG.info("Downloading to " + file.getCanonicalPath());
            try (var channel = new FileOutputStream(file).getChannel()) {
                channel.transferFrom(source, 0, Long.MAX_VALUE);
                return new LocalAttachment(file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL getUrl() throws MalformedURLException {
        return URI.create(attachment.getUrl()).toURL();
    }
}
