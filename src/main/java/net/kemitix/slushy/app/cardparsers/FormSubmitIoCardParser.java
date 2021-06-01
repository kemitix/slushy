package net.kemitix.slushy.app.cardparsers;

import com.julienvey.trello.domain.Card;
import lombok.Setter;
import net.kemitix.slushy.app.CardBodyCleaner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Card parser for froms submitted through https://formsubmit.io
 */
@ApplicationScoped
public class FormSubmitIoCardParser implements CardParser {


    private static final Pattern HEADING =
            Pattern.compile("^\\s*(?<heading>.*?)\\s:\\s*(?<value>.*?)\\s*$",
                    Pattern.MULTILINE);
    private static final String SIGNATURE = "Formsubmit.io team";
    private static final Pattern HEADER_PATTERN = Pattern.compile("^\\s*(.*?)\\s:\\s*(.*?)\\s*$");

    @Setter
    @Inject
    CardBodyCleaner cardBodyCleaner;

    @Override
    public boolean canHandle(Card card) {
        boolean containsSignature = card.getDesc().contains(SIGNATURE);
        boolean matchesHeadingFormat = Arrays.stream(card.getDesc().split("\n"))
                .anyMatch(line -> HEADER_PATTERN.matcher(line).matches());
        return containsSignature
                && matchesHeadingFormat;
    }

    @Override
    public Map<String, String> parse(Card card) {
        String body = cardBodyCleaner.clean(card.getDesc());
        Map<String, String> values = new HashMap<>();
        String[] lines = body.split("\n");
        AtomicReference<String> lastHeading = new AtomicReference<>("");
        Arrays.stream(lines)
                .forEach(line -> {
                    Matcher matcher = HEADING.matcher(line);
                    if (matcher.matches()) {
                        String heading = matcher.group("heading");
                        values.put(heading, matcher.group("value"));
                        lastHeading.set(heading);
                    } else {
                        String key = lastHeading.get();
                        if ("coverletter".equals(key)) {
                            values.put(key, values.get(key) + "\n" + line);
                        }
                    }
                });
        return values;
    }
}
