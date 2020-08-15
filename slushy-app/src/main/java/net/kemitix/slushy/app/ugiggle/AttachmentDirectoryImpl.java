package net.kemitix.slushy.app.ugiggle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class AttachmentDirectoryImpl implements AttachmentDirectory {

    private static final Logger LOG =
            Logger.getLogger(
                    AttachmentDirectoryImpl.class.getName());

    private Path dir;
    private List<File> toDelete = new ArrayList<>();

    @PostConstruct
    void init() throws IOException {
        dir = Files.createTempDirectory("attachments");
        LOG.info("Attachments directory: " + dir);
    }

    @Override
    public File createFile(File fileName) {
        File file = dir.resolve(fileName.getName()).toFile();
        LOG.info("Created attachment: " + file);
        toDelete.add(file);
        return file;
    }

    @PreDestroy
    public void deleteFiles() {
        toDelete.stream()
                .peek(file -> LOG.info("Deleting: " + file))
                .map(File::delete)
                .filter(deleted -> !deleted)
                .forEach(r -> LOG.warning("Could not delete file"));
        if (dir.toFile().delete()) {
            LOG.info("Deleted directory: " + dir);
        } else {
            LOG.warning("Could not delete directory: " + dir);
        }
    }
}
