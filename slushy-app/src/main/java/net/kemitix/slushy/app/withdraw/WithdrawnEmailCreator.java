package net.kemitix.slushy.app.withdraw;

import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.email.BodyCreator;
import net.kemitix.slushy.app.email.SubjectCreator;

public class WithdrawnEmailCreator
        implements SubjectCreator<Submission>, BodyCreator<Submission> {

    @Override
    public String subject(Submission source) {
        return String.format(
                "Submission Withdrawn: %s by %s",
                source.getTitle(),
                source.getByline()
        );
    }

    @Override
    public String bodyText(Submission source) {
        String template = "" +
                "Dear %s,\n\n" +
                "Thank you for having submitted your story, \"%s by %s\"\n\n." +
                "We have withdrawn your story as requested.\n\n" +
                "I hope you will consider submitting to us again.\n\n" +
                "Yours,\n" +
                "Paul Campbell - Editor\n" +
                "Cossmass Infinities - https://www.cossmass.com/";
        return String.format(template,
                source.getByline(),
                source.getTitle(),
                source.getByline());
    }

    @Override
    public String bodyHtml(Submission source) {
        String template = "" +
                "<html><head></head>\n" +
                "<body>\n" +
                "<p>Dear %s,</p>\n" +
                "<p>Thank you for having submitted your story, \"<strong>%s by %s</strong>\".</p>" +
                "<p>We have withdrawn your story as requested.</p>" +
                "<p>I hope you will consider submitting to us again.</p>" +
                "<p>Yours,<br/>\n" +
                "Paul Campbell - Editor<br/>\n" +
                "<strong><a href=\"https://www.cossmass.com/\">Cossmass Infinities</a></strong><p>\n" +
                "</body></html>";
        return String.format(template,
                source.getByline(),
                source.getTitle(),
                source.getByline());
    }

}
