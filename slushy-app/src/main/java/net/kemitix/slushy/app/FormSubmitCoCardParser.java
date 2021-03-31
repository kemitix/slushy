package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Card parser for froms submitted through https://formsubmit.co
 */
@ApplicationScoped
public class FormSubmitCoCardParser
        implements CardParser {

    private static final Pattern HEADING =
            Pattern.compile("^\\*{1,2}(?<heading>.*?):[\\*\\s]\\*$",
                    Pattern.MULTILINE);
    public static final String SIGNATURE = "[**FormSubmit Team**](https://formsubmit.co)";

    @Inject
    CardBodyCleaner cardBodyCleaner;

    @Override
    public boolean canHandle(Card card) {
        return card.getDesc().contains(SIGNATURE);
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
