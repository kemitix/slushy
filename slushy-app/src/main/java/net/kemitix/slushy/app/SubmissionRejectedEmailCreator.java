package net.kemitix.slushy.app;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubmissionRejectedEmailCreator
        implements SubjectCreator<Submission>, BodyCreator<Submission> {

    @Override
    public String subject(Submission source) {
        return String.format(
                "Submission Rejected: %s by %s",
                source.getTitle(),
                source.getByline());
    }

    @Override
    public String bodyText(Submission source) {
        return String.format("" +
                        "Dear %s,\n\n" +
                        "Thank you for submitting your story, \"%s\".\n\n" +
                        "Unfortunately, we are choosing not to use this story.\n\n" +
                        "Please feel free to submit (https://www.cossmass.com/submit/)\n" +
                        "another story that you would like us to consider for publication.\n" +
                        "Dates of our submission windows are in our guidelines.\n\n" +
                        "Yours,\n\n" +
                        "Paul Campbell - Editor\n\n" +
                        "Cossmass Infinities - https://www.cossmass.com/",
                source.getByline(),
                source.getTitle()
                );
    }

    @Override
    public String bodyHtml(Submission source) {
        return String.format("" +
                        "<html><head></head>" +
                        "<body>" +
                        "<p>Dear %s,</p>" +
                        "<p>Thank you for submitting your story, \"%s\".</p>" +
                        "<p>Unfortunately, we are choosing not to use this story.</p>" +
                        "<p>Please feel free to submit (https://www.cossmass.com/submit/)" +
                        " another story that you would like us to consider for publication." +
                        " Dates of our submission windows are in our guidelines.</p>" +
                        "<p>Yours,<br/>" +
                        "Paul Campbell - Editor<br/>" +
                        "<strong><a href=\"https://www.cossmass.com/\">Cossmass Infinities</a></strong></p>" +
                        "</body></html>",
                source.getByline(),
                source.getTitle()
        );
    }
}
