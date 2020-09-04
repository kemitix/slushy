package net.kemitix.slushy.app;

import java.io.File;

public interface Attachment {

    /**
     * The name of the attachment file.
     *
     * @return the name of the file
     */
    File getFilename();

    /**
     * Downlaods the file to local file system.
     *
     * @return the name of the local file.
     */
    LocalAttachment download();

    /**
     * The original filename.
     *
     * @return the name of the file originally
     */
    File getOriginalFilename();

}
