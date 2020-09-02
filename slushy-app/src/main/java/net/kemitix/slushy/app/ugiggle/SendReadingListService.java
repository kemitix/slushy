package net.kemitix.slushy.app.ugiggle;

import net.kemitix.slushy.app.Attachment;
import net.kemitix.slushy.app.ConversionService;
import net.kemitix.slushy.app.email.EmailService;
import net.kemitix.slushy.app.trello.TrelloCard;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.IOException;

//@ApplicationScoped
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
            emailService.sendAttachmentOnly("", "", attachment);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

}
