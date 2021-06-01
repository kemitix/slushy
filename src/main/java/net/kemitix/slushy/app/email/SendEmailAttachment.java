package net.kemitix.slushy.app.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.kemitix.trello.LocalAttachment;
import org.apache.camel.Handler;
import org.apache.camel.Header;

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
public class SendEmailAttachment {

    @Inject AmazonSimpleEmailService sesService;

    @Handler
    public void send(
            @NonNull @Header("SlushyRecipient") String recipient,
            @NonNull @Header("SlushySender") String sender,
            @NonNull @Header("SlushySubject") String subject,
            @NonNull @Header("SlushyReadableAttachment") LocalAttachment attachment
    ) throws MessagingException, IOException {
        log.info(String.format("send to %s, from %s", recipient, sender));
        SendRawEmailRequest request =
                requestWithAttachmentOnly(recipient, sender, attachment, subject);
        String name = attachment.getFilename().getName();
        log.info(String.format("Sending %s", name));
        sesService.sendRawEmail(request);
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
                .withDestinations(recipient)
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

    private Multipart mimeMultiPart(LocalAttachment attachment)
            throws IOException, MessagingException {
        MimeMultipart mimeMultipart = new MimeMultipart("mixed");
        mimeMultipart.addBodyPart(attachment(attachment));
        return mimeMultipart;
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

    private Session session() {
        return Session.getDefaultInstance(new Properties());
    }

    private BodyPart attachment(LocalAttachment attachment)
            throws MessagingException, IOException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        File fileName = attachment.getFilename();
        mimeBodyPart.attachFile(fileName);
        mimeBodyPart.setFileName(attachment.getFilename().getName());
        return mimeBodyPart;
    }

}
