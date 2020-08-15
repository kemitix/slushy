package net.kemitix.slushy.app.ugiggle;

import java.io.File;

public interface Attachment {

    /**
     * The name of the attachment file.
     *
     * @return the name of the file
     */
    File getFileName();

    /**
     * Downlaods the file to local file system.
     *
     * @return the name of the local file.
     */
    Attachment download();
}
