package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import net.kemitix.ugiggle.trello.Attachment;
import net.kemitix.ugiggle.trello.AttachmentDirectory;
import net.kemitix.ugiggle.trello.TrelloCard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;

@ApplicationScoped
public class AttachmentLoader {

    @Inject
    Trello trello;

    @Inject
    AttachmentDirectory attachmentDirectory;

    File load(Card card) {
        return TrelloCard.create(card, trello, attachmentDirectory)
                .findAttachments()
                .map(Attachment::download)
                .map(Attachment::getFileName)
                .findFirst()
                .orElseThrow();
    }

}
