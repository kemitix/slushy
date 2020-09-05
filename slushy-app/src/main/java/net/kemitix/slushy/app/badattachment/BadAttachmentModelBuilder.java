package net.kemitix.slushy.app.badattachment;

import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.ValidFileTypes;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class BadAttachmentModelBuilder {

    @Inject ValidFileTypes validFileTypes;

    Map<String, String> build(
            @Header("SlushySubmission") Submission submission
    ) {
        return Map.of(
                "title", submission.getTitle(),
                "byline", submission.getByline(),
                "validFileTypes", String.join(", ", validFileTypes.get())
        );
    }

}
