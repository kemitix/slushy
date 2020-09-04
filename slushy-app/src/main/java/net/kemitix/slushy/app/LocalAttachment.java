package net.kemitix.slushy.app;

import lombok.Getter;

import java.io.File;

/**
 * An attachment that has already been downloaded.
 *
 * Calling download, is a noop that returns the local file.
 */
@Getter
public class LocalAttachment
        implements Attachment {

    private final File filename;
    private final File originalFilename;

    public LocalAttachment(
            File filename,
            File originalFilename
    ) {
        this.filename = filename;
        this.originalFilename = originalFilename;
    }

    @Override
    public LocalAttachment download() {
        return this;
    }
}
