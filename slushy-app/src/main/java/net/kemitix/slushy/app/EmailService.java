package net.kemitix.slushy.app;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailService {
    void send(
            String recipient,
            String sender,
            Attachment attachment
    ) throws MessagingException, IOException;
}
