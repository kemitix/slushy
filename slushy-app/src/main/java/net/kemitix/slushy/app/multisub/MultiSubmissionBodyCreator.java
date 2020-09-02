package net.kemitix.slushy.app.multisub;

import net.kemitix.slushy.app.email.BodyCreator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MultiSubmissionBodyCreator
        implements BodyCreator<RejectedMultipleSubmission> {
    @Override
    public String bodyText(RejectedMultipleSubmission source) {
        return String.format("" +
                        "Dear %s,\n" +
                        "\n" +
                        "Thank you for submitting your story, \"%s\".\n" +
                        "\n" +
                        "Unfortunately, we can't accept your submission at this time." +
                        " We already have one of your stories submitted:\n" +
                        "\n" +
                        "* %s\n" +
                        "\n" +
                        "We don't accept multiple submissions.\n" +
                        "\n" +
                        "If your existing story is rejected then please resubmit this story then.\n" +
                        "\n" +
                        "Yours,\n" +
                        "Paul Campbell - Editor\n" +
                        "Cossmass Infinities - https://www.cossmass.com/",
                source.getCurrent().getByline(),
                source.getCurrent().getTitle(),
                source.getExisting().getTitle());
    }

    @Override
    public String bodyHtml(RejectedMultipleSubmission source) {
        return String.format("" +
                        "<html><head></head>" +
                        "<body>" +
                        "<p>Dear %s,</p>" +
                        "<p>Thank you for submitting your story, \"%s\".</p>" +
                        "<p>Unfortunately, we can't accept your submission at this time." +
                        " We already have one of your stories submitted:</p>" +
                        "<ul>" +
                        "  <li>%s</li>" +
                        "</ul>" +
                        "<p>We don't accept multiple submissions.</p>" +
                        "<p>If your existing story is rejected then please resubmit this story then.</p>" +
                        "<p>Yours,<br/>" +
                        "Paul Campbell - Editor<br/>" +
                        "<strong><a href=\"https://www.cossmass.com/\">Cossmass Infinities</a></strong></p>" +
                        "</body></html>",
                source.getCurrent().getByline(),
                source.getCurrent().getTitle(),
                source.getExisting().getTitle());
    }
}
