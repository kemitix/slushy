package net.kemitix.slushy.app.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.Attachment;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

@Log
@ApplicationScoped
public class AmazonSesService implements EmailService {

    @Inject AmazonSimpleEmailService sesService;

    @Override
    public void send(
            String recipient,
            String sender,
            String subject,
            String body,
            String bodyHtml
    ) {
        log.info(String.format("send to %s, from %s", recipient, sender));
        SendEmailRequest request =
                request(recipient, sender, subject, body, bodyHtml);
        log.info(String.format("Sending %s", subject));
        sesService.sendEmail(request);
    }

    @Override
    public void sendAttachmentOnly(
            String recipient,
            String sender,
            Attachment attachment
    ) throws MessagingException, IOException {
        log.info(String.format("send to %s, from %s", recipient, sender));
        SendRawEmailRequest request =
                requestWithAttachmentOnly(recipient, sender, attachment);
        String name = attachment.getFileName().getName();
        log.info(String.format("Sending %s", name));
        sesService.sendRawEmail(request);
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

    private SendRawEmailRequest requestWithAttachmentOnly(
            String recipient,
            String sender,
            Attachment attachment
    ) throws MessagingException, IOException {
        RawMessage rawMessage =
                rawMessageWithAttachmentOnly(recipient, sender, attachment);
        return new SendRawEmailRequest()
                .withDestinations(recipient)
                .withSource(sender)
                .withRawMessage(rawMessage);
    }

    private RawMessage rawMessage(
            String recipient,
            String sender,
            String body
    ) throws MessagingException, IOException {
        byte[] messageStream = messageStream(
                new InternetAddress(recipient),
                new InternetAddress(sender),
                mimeMultiPart(body));
        return new RawMessage(ByteBuffer.wrap(messageStream));
    }

    private RawMessage rawMessageWithAttachmentOnly(
            String recipient,
            String sender,
            Attachment attachment
    ) throws MessagingException, IOException {
        byte[] messageStream = messageStream(
                new InternetAddress(recipient),
                new InternetAddress(sender),
                mimeMultiPart(attachment));
        return new RawMessage(ByteBuffer.wrap(messageStream));
    }

    private byte[] messageStream(
            Address recipient,
            Address sender,
            Multipart content
    ) throws MessagingException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        message(recipient, sender, content).writeTo(stream);
        return stream.toByteArray();
    }

    private MimeMessage message(
            Address recipient,
            Address sender,
            Multipart content
    ) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(session());
        message.setFrom(sender);
        message.setRecipient(Message.RecipientType.TO, recipient);
        message.setContent(content);
        return message;
    }

    private Multipart mimeMultiPart(Attachment attachment)
            throws IOException, MessagingException {
        MimeMultipart mimeMultipart = new MimeMultipart("mixed");
        mimeMultipart.addBodyPart(attachment(attachment));
        return mimeMultipart;
    }
    private Multipart mimeMultiPart(String body) throws MessagingException {
        MimeMultipart mimeMultipart = new MimeMultipart("mixed");
        MimeBodyPart part = new MimeBodyPart();
        part.setContent(body, "plain/text");
        mimeMultipart.addBodyPart(part);
        return mimeMultipart;
    }

    private BodyPart attachment(Attachment attachment)
            throws MessagingException, IOException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        File fileName = attachment.download().getFileName();
        mimeBodyPart.attachFile(fileName);
        mimeBodyPart.setFileName(attachment.getFileName().getName());
        return mimeBodyPart;
    }

    private Session session() {
        return Session.getDefaultInstance(new Properties());
    }

}
