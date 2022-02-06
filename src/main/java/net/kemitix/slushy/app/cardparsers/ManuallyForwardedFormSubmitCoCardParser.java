package net.kemitix.slushy.app.cardparsers;

import com.julienvey.trello.domain.Card;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Setter;
import net.kemitix.slushy.app.CardBodyCleaner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Card parser for forms submitted through https://formsubmit.co
 */
@ApplicationScoped
public class ManuallyForwardedFormSubmitCoCardParser
        implements CardParser {

    private static final Pattern HEADING =
            Pattern.compile("^\\*(?<heading>.*?):\\s\\*$",
                    Pattern.MULTILINE);
    private static final String SIGNATURE = "*FormSubmit Team* <https://formsubmit.co>";
    private static final Pattern HEADER_PATTERN = Pattern.compile("^\\*(.*?):\\s\\*$");

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
        AtomicReference<String> header = new AtomicReference<>("preamble");
        Arrays.stream(body.split("\n"))
                .forEach(line -> {
                    Matcher matcher = HEADING.matcher(line);
                    if (matcher.matches()) {
                        header.set(matcher.group("heading"));
                    } else {
                        if (!"".equals(line)
                                && !"------------------------------".equals(line)
                        ) {
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
