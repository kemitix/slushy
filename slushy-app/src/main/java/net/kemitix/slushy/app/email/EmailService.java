package net.kemitix.slushy.app.email;

public interface EmailService {

    void send(
            String recipient,
            String sender,
            String subject,
            String body,
            String bodyHtml
    );

}
