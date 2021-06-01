package net.kemitix.slushy.app;

import java.io.File;

public class MissingAttachment
        extends net.kemitix.trello.LocalAttachment {
    public MissingAttachment() {
        super(new File(""), new File(""), 0L);
    }
}
