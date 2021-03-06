package net.kemitix.slushy.app.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class SendEmail {

    @Inject AmazonSimpleEmailService sesService;

    @Handler
    public void send(
            @NonNull @Header("SlushyRecipient") String recipient,
            @NonNull @Header("SlushySender") String sender,
            @NonNull @Header("SlushySubject") String subject,
            @NonNull @Header("SlushyBody") String body,
            @NonNull @Header("SlushyBodyHtml") String bodyHtml
    ) {
        SendEmailRequest request =
                request(recipient, sender, subject, body, bodyHtml);
        log.info(String.format("Sending to %s: %s", recipient, subject));
        sesService.sendEmail(request);
    }

    private SendEmailRequest request(
            String recipient,
            String sender,
            String subject,
            String bodyText,
            String bodyHtml
    ) {
        return new SendEmailRequest()
                .withDestination(to(recipient, sender))
                .withSource(sender)
                .withMessage(message(subject, body(bodyText, bodyHtml)));
    }

    private com.amazonaws.services.simpleemail.model.Message message(
            String subject,
            Body body
    ) {
        return new com.amazonaws.services.simpleemail.model.Message()
                .withSubject(content(subject))
                .withBody(body);
    }

    private Body body(String body, String bodyHtml) {
        return new Body()
                .withText(content(body))
                .withHtml(content(bodyHtml));
    }

    private Destination to(String recipient, String sender) {
        return new Destination()
                .withToAddresses(recipient)
                .withBccAddresses(sender);
    }

    private Content content(String subject) {
        return new Content().withData(subject);
    }

}
