package net.kemitix.slushy.app;

import com.julienvey.trello.NotFoundException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Card;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AttachmentLoader {

    Trello trello;
    private List<String> acceptedMimes;

    File load(Submission submission) {
        //TODO
        // list all attachments
        // Move this over to Submission
        List<Attachment> cardAttachments = trello.getCardAttachments(card.getId());
        // find the first that matches accepted extension
        Attachment att = cardAttachments.stream()
                .filter(attachment -> acceptedMimes.contains(attachment.getMimeType()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No valid attachment found"));
        // download

        // return File
        return null;
    }

}
