package net.kemitix.slushy.app.badattachment;

import net.kemitix.slushy.app.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InvalidAttachmentEmailCreator
        implements SubjectCreator<Submission>, BodyCreator<Submission> {

    @Inject ValidFileTypes validFileTypes;

    @Override
    public String subject(Submission source) {
        return String.format("Submission Rejected: %s by %s",
                source.getTitle(),
                source.getByline());
    }

    @Override
    public String bodyText(Submission source) {
        return String.format("" +
                        "Dear %s,\n\n" +
                        "Thank you for submitting your story, \"%s by %s\".\n\n" +
                        "Unfortunately, we are not able to accept it in this file format.\n\n" +
                        "Please feel free to resubmit (https://www.cossmass.com/submit/)\n" +
                        "once you have converted to one of these formats:\n" +
                        " - %s\n" +
                        "Yours,\n\n" +
                        "Paul Campbell - Editor\n\n" +
                        "Cossmass Infinities - https://www.cossmass.com/",
                source.getByline(),
                source.getTitle(),
                source.getByline(),
                String.join(", ", validFileTypes.get())
        );
    }

    @Override
    public String bodyHtml(Submission source) {
        return String.format("" +
                        "<html><head></head>" +
                        "<body>" +
                        "<p>Dear %s,</p>" +
                        "<p>Thank you for submitting your story, \"%s by %s\".</p>" +
                        "<p>Unfortunately, we are not able to accept it in this file format.</p>" +
                        "<p>Please feel free to resubmit (https://www.cossmass.com/submit/)" +
                        " once you have converted to one of these formats:</p>" +
                        "<ul><li>%s</li></ul>" +
                        "<p>Yours,<br/>" +
                        "Paul Campbell - Editor<br/>" +
                        "<strong><a href=\"https://www.cossmass.com/\">Cossmass Infinities</a></strong></p>" +
                        "</body></html>",
                source.getByline(),
                source.getTitle(),
                source.getByline(),
                String.join(", ", validFileTypes.get())
        );
    }
}
