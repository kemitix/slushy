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
    private final long length;

    public LocalAttachment(
            File filename,
            File originalFilename,
            long length
    ) {
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.length = length;
    }

    boolean isCorrupt() {
        return length == 0;
    }

    @Override
    public LocalAttachment download() {
        return this;
    }
}
