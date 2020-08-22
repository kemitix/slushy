package net.kemitix.slushy.app.multisub;

import net.kemitix.slushy.app.SubjectCreator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MultiSubmissionSubjectCreator
        implements SubjectCreator<RejectedMultipleSubmission> {
    @Override
    public String subject(RejectedMultipleSubmission source) {
        return String.format("Multiple Submission Rejected: %s by %s",
                source.getCurrent().getTitle(),
                source.getCurrent().getByline());
    }
}
