package net.kemitix.slushy.app;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubmissionReceivedBodyCreator
        implements BodyCreator<Submission> {

    @Override
    public String bodyText(Submission submission) {
        String template = "" +
                "Dear %s,\n\n" +
                "Thank you for submitting your story, \"%s\".\n\n" +
                "It has been added to our system and we look forward to reading it.\n\n" +
                "Please allow 30 days for a reply.\n\n" +
                "Yours,\n" +
                "Paul Campbell - Editor\n" +
                "Cossmass Infinities - https://www.cossmass.com/";
        return String.format(template,
                submission.getByline(),
                submission.getTitle());
    }

    @Override
    public String bodyHtml(Submission submission) {
        String template = "" +
                "<html><head></head>\n" +
                "<body>\n" +
                "<p>Dear %s,</p>\n" +
                "<p>Thank you for submitting your story, \"<strong>%s</strong>\".</p>\n" +
                "<p>It has been added to our system and we look forward to reading it.</p>\n" +
                "<p>Please allow 30 days for a reply.</p>\n" +
                "<p>Yours,<br/>\n" +
                "Paul Campbell - Editor<br/>\n" +
                "<strong><a href=\"https://www.cossmass.com/\">Cossmass Infinities</a></strong><p>\n" +
                "</body></html>";
        return String.format(template,
                submission.getByline(),
                submission.getTitle());
    }

}