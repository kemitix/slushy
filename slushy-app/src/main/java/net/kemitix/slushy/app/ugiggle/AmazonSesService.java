package net.kemitix.slushy.app.ugiggle;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;

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
import java.util.logging.Logger;

@ApplicationScoped
public class AmazonSesService implements EmailService {

    private final static Logger LOG =
            Logger.getLogger(
                    AmazonSesService.class.getName());

    private UGiggleConfig config;
    private AmazonSimpleEmailService sesService;

    public AmazonSesService() {
    }

    @Inject
    public AmazonSesService(
            UGiggleConfig config,
            AmazonSimpleEmailService sesService
    ) {
        this.config = config;
        this.sesService = sesService;
    }

    @Override
    public void send(Attachment attachment) throws MessagingException, IOException {
        String recipient = config.getRecipient();
        SendRawEmailRequest request = request(recipient, attachment);
        String name = attachment.getFileName().getName();
        LOG.info(String.format("Sending %s", name));
        sesService.sendRawEmail(request);
    }

    private SendRawEmailRequest request(
            String recipient,
            Attachment attachment
    ) throws MessagingException, IOException {
        RawMessage rawMessage = rawMessage(recipient, attachment);
        return new SendRawEmailRequest()
                .withDestinations(recipient)
                .withSource(config.getSender())
                .withRawMessage(rawMessage);
    }

    private RawMessage rawMessage(String recipient, Attachment attachment) throws MessagingException, IOException {
        byte[] messageStream = messageStream(new InternetAddress(recipient), attachment);
        return new RawMessage(ByteBuffer.wrap(messageStream));
    }

    private byte[] messageStream(Address recipient, Attachment attachment) throws MessagingException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        message(recipient, attachment).writeTo(stream);
        return stream.toByteArray();
    }

    private MimeMessage message(Address recipient, Attachment attachment) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(session());
        message.setFrom(new InternetAddress(config.getSender()));
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
