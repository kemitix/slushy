package net.kemitix.slushy.app.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.LocalAttachment;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
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
            @NonNull String recipient,
            @NonNull String sender,
            @NonNull String subject,
            @NonNull LocalAttachment attachment
    ) throws MessagingException, IOException {
        log.info(String.format("send to %s, from %s", recipient, sender));
        SendRawEmailRequest request =
                requestWithAttachmentOnly(recipient, sender, attachment, subject);
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
            LocalAttachment attachment,
            String subject
    ) throws MessagingException, IOException {
        RawMessage rawMessage = rawMessageWithAttachmentOnly(
                recipient, sender, attachment, subject);
        return new SendRawEmailRequest()
                .withDestinations(recipient, sender)
                .withSource(sender)
                .withRawMessage(rawMessage);
    }

    private RawMessage rawMessageWithAttachmentOnly(
            String recipient,
            String sender,
            LocalAttachment attachment,
            String subject
    ) throws MessagingException, IOException {
        byte[] messageStream = messageStream(
                new InternetAddress(recipient),
                new InternetAddress(sender),
                mimeMultiPart(attachment),
                subject);
        return new RawMessage(ByteBuffer.wrap(messageStream));
    }

    private byte[] messageStream(
            Address recipient,
            Address sender,
            Multipart content,
            String subject
    ) throws MessagingException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        message(recipient, sender, content, subject).writeTo(stream);
        return stream.toByteArray();
    }

    private MimeMessage message(
            Address recipient,
            Address sender,
            Multipart content,
            String subject
    ) throws MessagingException {
        MimeMessage message = new MimeMessage(session());
        message.setFrom(sender);
        message.setSubject(subject);
        message.setRecipient(Message.RecipientType.TO, recipient);
        message.setContent(content);
        return message;
    }

    private Multipart mimeMultiPart(LocalAttachment attachment)
            throws IOException, MessagingException {
        MimeMultipart mimeMultipart = new MimeMultipart("mixed");
        mimeMultipart.addBodyPart(attachment(attachment));
        return mimeMultipart;
    }

    private BodyPart attachment(LocalAttachment attachment)
            throws MessagingException, IOException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        File fileName = attachment.getFileName();
        mimeBodyPart.attachFile(fileName);
        mimeBodyPart.setFileName(attachment.getFileName().getName());
        return mimeBodyPart;
    }

    private Session session() {
        return Session.getDefaultInstance(new Properties());
    }

}
