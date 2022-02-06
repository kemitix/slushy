package net.kemitix.slushy.app.email;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kemitix.trello.LocalAttachment;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

@Log
@ApplicationScoped
public class SendEmailAttachment {

    @Inject
    SesClient sesClient;

    @Handler
    public void send(
            @NonNull @Header("SlushyRecipient") String recipient,
            @NonNull @Header("SlushySender") String sender,
            @NonNull @Header("SlushySubject") String subject,
            @NonNull @Header("SlushyReadableAttachment") LocalAttachment attachment
    ) throws MessagingException, IOException {
        log.info(String.format("send to %s, from %s", recipient, sender));
        String name = attachment.getFilename().getName();
        log.info(String.format("Sending %s", name));
        sesClient.sendRawEmail(request -> request
                .source(sender)
                .destinations(recipient)
                .rawMessage(rawMessage(recipient, sender, subject, attachment)));
    }

    @SneakyThrows
    private RawMessage rawMessage(
            String recipient,
            String sender,
            String subject,
            LocalAttachment attachment
    ) {
        return RawMessage.builder()
                .data(SdkBytes.fromByteBuffer(ByteBuffer.wrap(messageStream(
                        new InternetAddress(recipient),
                        new InternetAddress(sender),
                        mimeMultiPart(attachment),
                        subject))))
                .build();
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
