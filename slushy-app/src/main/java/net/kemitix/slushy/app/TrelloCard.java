package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;

import java.util.stream.Stream;

public class TrelloCard {
    private final com.julienvey.trello.domain.Card tcard;
    private final Trello trello;
    private AttachmentDirectory attachmentDir;

    private TrelloCard(
            Card tcard,
            Trello trello,
            AttachmentDirectory attachmentDir
    ) {
        this.tcard = tcard;
        this.trello = trello;
        this.attachmentDir = attachmentDir;
    }

    public static TrelloCard create(
            Card tcard,
            Trello trello,
            AttachmentDirectory attachmentDir
    ) {
        return new TrelloCard(tcard, trello, attachmentDir);
    }

    public Stream<Attachment> findAttachments() {
        return trello.getCardAttachments(tcard.getId()).stream()
                .map(attachment -> TrelloAttachment
                        .create(attachment, tcard, attachmentDir));
    }

    public String getName() {
        return tcard.getName();
    }
}
