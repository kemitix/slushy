package net.kemitix.slushy.app.inbox;

import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.email.BodyCreator;
import net.kemitix.slushy.app.email.SubjectCreator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubmissionReceivedEmailCreator
        implements SubjectCreator<Submission>, BodyCreator<Submission> {

    @Override
    public String subject(Submission submission) {
        return String.format(
                "Submission Received: %s by %s",
                submission.getTitle(),
                submission.getByline());
    }

    @Override
    public String bodyText(Submission submission) {
        String template = "" +
                "Dear %s,\n\n" +
                "Thank you for submitting your story, \"%s by %s\".\n\n" +
                "It has been added to our system and we look forward to reading it.\n\n" +
                "Please allow 30 days for a reply.\n\n" +
                "Yours,\n" +
                "Paul Campbell - Editor\n" +
                "Cossmass Infinities - https://www.cossmass.com/";
        return String.format(template,
                submission.getByline(),
                submission.getTitle(),
                submission.getByline()
        );
    }

    @Override
    public String bodyHtml(Submission submission) {
        String template = "" +
                "<html><head></head>\n" +
                "<body>\n" +
                "<p>Dear %s,</p>\n" +
                "<p>Thank you for submitting your story, \"<strong>%s by %s</strong>\".</p>\n" +
                "<p>It has been added to our system and we look forward to reading it.</p>\n" +
                "<p>Please allow 30 days for a reply.</p>\n" +
                "<p>Yours,<br/>\n" +
                "Paul Campbell - Editor<br/>\n" +
                "<strong><a href=\"https://www.cossmass.com/\">Cossmass Infinities</a></strong><p>\n" +
                "</body></html>";
        return String.format(template,
                submission.getByline(),
                submission.getTitle(),
                submission.getByline()
        );
    }

}
