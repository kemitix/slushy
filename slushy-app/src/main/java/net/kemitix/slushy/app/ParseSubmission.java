package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Card;
import lombok.NonNull;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Map;
import java.util.UnknownFormatConversionException;

@ApplicationScoped
public class ParseSubmission {

    @Inject Now now;
    @Inject TrelloBoard trelloBoard;
    @Inject ValidFileTypes validFileTypes;
    @Inject Instance<CardParser> cardParsers;

    @Handler
    public Submission parse(
            @NonNull @Header("SlushyCard") Card card
    ) {
        Map<String, String> body = parseBody(card);
        return Submission.builder()
                .id(card.getIdShort())
                .title(body.get("storytitle"))
                .byline(body.get("byline"))
                .realName(body.get("name"))
                .email(body.get("email"))
                .paypal(body.get("paypal"))
                .wordLength(WordLengthBand.parse(body.get("wordcount")))
                .coverLetter(body.get("coverletter"))
                .contract(Contract.parse(body.get("contract")))
                .submittedDate(now.get())
                .document(getAttachmentUrl(card))
                .logLine(body.getOrDefault("logline", ""))
                .genre(Genre.parse(body.getOrDefault("genre", Genre.Unknown.toString())))
                ;
    }

    private String getAttachmentUrl(Card card) {
        return trelloBoard.getAttachments(card)
                .stream()
                .map(Attachment::getUrl)
                .filter(this::validExtension)
                .findFirst()
                .orElse(null);
    }

    // Kindle can handle directly or that can be converted
    private boolean validExtension(String url) {
        String filename = url.toLowerCase();
        return validFileTypes.get().stream()
                .map(String::toLowerCase)
                .map(e -> "." + e)
                .anyMatch(filename::endsWith);
    }

    private Map<String, String> parseBody(Card card) {
        Map<String, String> o = cardParsers.stream()
                .filter(parser -> parser.canHandle(card))
                .findFirst()
                .map(parser -> parser.parse(card))
                .orElseThrow(() ->
                        new UnknownCardFormatException(card));
        return o;
    }
}
