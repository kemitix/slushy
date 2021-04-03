package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import net.kemitix.trello.Attachment;
import net.kemitix.trello.AttachmentDirectory;
import net.kemitix.trello.LocalAttachment;
import net.kemitix.trello.CardWithAttachments;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AttachmentLoader {

    @Inject
    Trello trello;

    @Inject
    AttachmentDirectory attachmentDirectory;

    public LocalAttachment load(Card card) {
        return CardWithAttachments.create(card, trello, attachmentDirectory)
                .findAttachments()
                .map(Attachment::download)
                .findFirst()
                .orElseGet(MissingAttachment::new);
    }

}
