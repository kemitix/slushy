package net.kemitix.slushy.app.ugiggle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.IOException;

@ApplicationScoped
public class SendReadingListService {

    @Inject
    ReadingListService readingListService;
    @Inject
    EmailService emailService;
    @Inject
    ConversionService conversionService;

    public void run(String[] args) {
        readingListService.getReadingList()
                .flatMap(TrelloCard::findAttachments)
                .map(conversionService::convert)
                .forEach(this::sendEmail);
    }

    private void sendEmail(Attachment attachment) {
        try {
            emailService.send(attachment);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

}
