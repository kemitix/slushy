package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import net.kemitix.slushy.app.trello.TrelloCard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AttachmentLoader {

    @Inject
    Trello trello;

    @Inject
    AttachmentDirectory attachmentDirectory;

    public LocalAttachment load(Card card) {
        return TrelloCard.create(card, trello, attachmentDirectory)
                .findAttachments()
                .map(Attachment::download)
                .findFirst()
                .orElseThrow();
    }

}
