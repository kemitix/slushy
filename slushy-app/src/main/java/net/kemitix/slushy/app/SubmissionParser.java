package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
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

    Submission parse(Card card) {
        log.info("CARD " + card.getName());
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
        return Submission.builder()
                .title(values.get("storytitle"))
                .byline(values.get("byline"))
                .realName(values.get("name"))
                .email(values.get("email"))
                .paypal(values.get("paypal"))
                .wordLength(WordLengthBand.parse(values.get("wordcount")))
                .coverLetter(values.get("coverletter"))
                .contract(Contract.parse(values.get("contract")))
                .submittedDate(nowSupplier.get());
    }
}
