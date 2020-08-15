package net.kemitix.slushy.app.ugiggle;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailService {
    void send(Attachment attachment) throws MessagingException, IOException;
}
