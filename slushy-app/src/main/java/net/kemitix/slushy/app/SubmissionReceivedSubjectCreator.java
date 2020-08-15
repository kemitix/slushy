package net.kemitix.slushy.app;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@SubmissionReceived
public class SubmissionReceivedSubjectCreator
        implements SubjectCreator<Submission> {

    @Override
    public String subject(Submission submission) {
        return String.format(
                "Submission Received: %s by %s",
                submission.getTitle(),
                submission.getByline());
    }

}
