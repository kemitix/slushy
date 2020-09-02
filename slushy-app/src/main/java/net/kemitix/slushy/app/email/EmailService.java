package net.kemitix.slushy.app.email;

import net.kemitix.slushy.app.Attachment;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailService {
    void sendAttachmentOnly(
            String recipient,
            String sender,
            Attachment attachment
    ) throws MessagingException, IOException;

    void send(
            String recipient,
            String sender,
            String subject,
            String body,
            String bodyHtml
    );

}
