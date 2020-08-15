package net.kemitix.slushy.app.ugiggle;

import java.io.File;

/**
 * An attachment that has already been downloaded.
 *
 * Calling download, is a noop that returns the local file.
 */
public class LocalAttachment
        implements Attachment {

    private final File filename;

    public LocalAttachment(File filename) {
        this.filename = filename;
    }

    @Override
    public File getFileName() {
        return filename;
    }

    @Override
    public Attachment download() {
        return this;
    }
}
