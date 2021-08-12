package net.kemitix.slushy.app;

import net.kemitix.trello.LocalAttachment;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class AttachmentDownloadValidator {

    public Optional<LocalAttachment> apply(LocalAttachment localAttachment) {
        return Optional.of(localAttachment);
    }

}
