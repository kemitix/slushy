package net.kemitix.slushy.app;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubmissionRejectedSubjectCreator
        implements SubjectCreator<Submission> {
    @Override
    public String subject(Submission source) {
        return String.format(
                "Submission Rejected: %s by %s",
                source.getTitle(),
                source.getByline());
    }
}
