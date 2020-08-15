package net.kemitix.slushy.app;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubjectCreator {

    String subject(Submission submission) {
        return String.format(
                "Submission Received: %s by %s",
                submission.getTitle(),
                submission.getByline());
    }

}
