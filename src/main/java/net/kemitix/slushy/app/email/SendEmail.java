package net.kemitix.slushy.app.email;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class SendEmail {

    @Inject SesClient sesClient;

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
        sesClient.sendEmail(request);
    }

    private SendEmailRequest request(
            String recipient,
            String sender,
            String subject,
            String bodyText,
            String bodyHtml
    ) {
        return SendEmailRequest.builder()
                .destination(Destination.builder()
                        .toAddresses(recipient, sender)
                        .build())
                .source(sender)
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder()
                                .text(Content.builder().data(bodyText).build())
                                .html(Content.builder().data(bodyHtml).build())
                                .build())
                        .build())
                .build();
    }

}
