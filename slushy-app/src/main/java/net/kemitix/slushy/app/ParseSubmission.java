package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Card;
import lombok.NonNull;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class ParseSubmission {

    private static final Pattern HEADING =
            Pattern.compile("^\\*\\*(?<heading>.*?):\\*\\*$",
                    Pattern.MULTILINE);

    @Inject Now now;
    @Inject TrelloBoard trelloBoard;
    @Inject ValidFileTypes validFileTypes;
    @Inject CardBodyCleaner cardBodyCleaner;

    @Handler
    public Submission parse(
            @NonNull @Header("SlushyCard") Card card
    ) {
        Map<String, String> body = parseBody(card);
        return Submission.builder()
                .title(body.get("storytitle"))
                .byline(body.get("byline"))
                .realName(body.get("name"))
                .email(body.get("email"))
                .paypal(body.get("paypal"))
                .wordLength(WordLengthBand.parse(body.get("wordcount")))
                .coverLetter(body.get("coverletter"))
                .contract(Contract.parse(body.get("contract")))
                .submittedDate(now.get())
                .document(getAttachmentUrl(card));
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
        String body = cardBodyCleaner.clean(card.getDesc());
        Map<String, String> values = new HashMap<>();
        AtomicReference<String> header = new AtomicReference<>("preamble");
        Arrays.stream(body.split("\n"))
                .forEach(line -> {
                    Matcher matcher = HEADING.matcher(line);
                    if (matcher.matches()) {
                        header.set(matcher.group("heading"));
                    } else {
                        if (!"".equals(line)) {
                            String key = header.get();
                            if (values.containsKey(key)) {
                                values.put(key, values.get(key) + "\n\n" + line);
                            } else {
                                values.put(key, line);
                            }
                        }
                    }
                });
        return values;
    }
}
