package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Card;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
@ApplicationScoped
public class SubmissionParser {

    private static final Pattern HEADING =
            Pattern.compile("^\\*\\*(?<heading>.*?):\\*\\*$",
                    Pattern.MULTILINE);

    @Inject Supplier<Instant> nowSupplier;
    @Inject TrelloBoard trelloBoard;
    List<String> acceptedFileExtensions = List.of("docx", "doc", "odt");

    Submission parse(Card card) {
        log.info("CARD " + card.getName());
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
                .submittedDate(nowSupplier.get())
                .document(getAttachmentUrl(card));
    }

    private String getAttachmentUrl(Card card) {
        return trelloBoard.getAttachments(card)
                .stream()
                .map(Attachment::getUrl)
                .filter(this::validExtension)
                .findFirst()
                .orElseThrow();
    }

    private boolean validExtension(String url) {
        return acceptedFileExtensions
                .stream()
                .anyMatch(extension -> url.endsWith("." + extension));
    }

    private Map<String, String> parseBody(Card card) {
        String body = card.getDesc();
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
