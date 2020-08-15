package net.kemitix.slushy.app;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.extern.java.Log;

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
            Attachment attachment
    ) throws MessagingException, IOException {
        SendRawEmailRequest request = request(recipient, sender, attachment);
        String name = attachment.getFileName().getName();
        log.info(String.format("Sending %s", name));
        sesService.sendRawEmail(request);
    }

    private SendRawEmailRequest request(
            String recipient,
            String sender,
            Attachment attachment
    ) throws MessagingException, IOException {
        RawMessage rawMessage = rawMessage(recipient, sender, attachment);
        return new SendRawEmailRequest()
                .withDestinations(recipient)
                .withSource(sender)
                .withRawMessage(rawMessage);
    }

    private RawMessage rawMessage(
            String recipient,
            String sender,
            Attachment attachment
    ) throws MessagingException, IOException {
        byte[] messageStream = messageStream(
                new InternetAddress(recipient),
                new InternetAddress(sender),
                attachment);
        return new RawMessage(ByteBuffer.wrap(messageStream));
    }

    private byte[] messageStream(
            Address recipient,
            Address sender,
            Attachment attachment
    ) throws MessagingException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        message(recipient, sender, attachment).writeTo(stream);
        return stream.toByteArray();
    }

    private MimeMessage message(
            Address recipient,
            Address sender,
            Attachment attachment
    ) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(session());
        message.setFrom(sender);
        message.setRecipient(Message.RecipientType.TO, recipient);
        message.setContent(mimeMultiPart(attachment));
        return message;
    }

    private Multipart mimeMultiPart(Attachment attachment) throws IOException, MessagingException {
        MimeMultipart mimeMultipart = new MimeMultipart("mixed");
        mimeMultipart.addBodyPart(attachment(attachment));
        return mimeMultipart;
    }

    private BodyPart attachment(Attachment attachment) throws MessagingException, IOException {
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
