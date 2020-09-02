package net.kemitix.slushy.app.hold;

import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.email.BodyCreator;
import net.kemitix.slushy.app.email.SubjectCreator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubmissionHoldEmailCreator
        implements SubjectCreator<Submission>, BodyCreator<Submission> {

    @Override
    public String subject(Submission source) {
        return String.format(
                "Submission Held: %s by %s",
                source.getTitle(),
                source.getByline());
    }

    @Override
    public String bodyText(Submission source) {
        return String.format("" +
                        "Dear %s,\n\n" +
                        "Thank you for submitting your story, \"%s by %s\".\n\n" +
                        "We have read your story and we liked it.\n" +
                        "However, we haven't yet decided if we want to select it" +
                        " for inclusion in an upcoming issue.\n" +
                        "We appreciate your time, which is why we accept sim-subs," +
                        " and encourage you to continue to submit your story to" +
                        " other markets.\n" +
                        "We will try to make a final decision as soon as possible" +
                        " and let you know.\n\n" +
                        "Please do let us know if you manage to place your story" +
                        " elsewhere." +
                        "Yours,\n\n" +
                        "Paul Campbell - Editor\n\n" +
                        "Cossmass Infinities - https://www.cossmass.com/",
                source.getByline(),
                source.getTitle(),
                source.getByline()
                );
    }

    @Override
    public String bodyHtml(Submission source) {
        return String.format("" +
                        "<html><head></head>" +
                        "<body>" +
                        "<p>Dear %s,</p>" +
                        "<p>Thank you for submitting your story, \"%s by %s\".</p>" +
                        "<p>We have read your story and we liked it." +
                        " However, we haven't yet decided if we want to select it" +
                        " for inclusion in an upcoming issue." +
                        " We appreciate your time, which is why we accept sim-subs," +
                        " and encourage you to continue to submit your story to" +
                        " other markets." +
                        " We will try to make a final decision as soon as possible" +
                        " and let you know.<p>" +
                        "<p>Please do let us know if you manage to place your story" +
                        " elsewhere.</p>" +
                        "<p>Yours,<br/>" +
                        "Paul Campbell - Editor<br/>" +
                        "<strong><a href=\"https://www.cossmass.com/\">Cossmass Infinities</a></strong></p>" +
                        "</body></html>",
                source.getByline(),
                source.getTitle(),
                source.getByline()
        );
    }

}
