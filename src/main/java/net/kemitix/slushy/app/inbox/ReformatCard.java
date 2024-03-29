package net.kemitix.slushy.app.inbox;

import lombok.NonNull;
import net.kemitix.slushy.app.Now;
import net.kemitix.slushy.app.SlushyHeader;
import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ReformatCard {

    public static final String ORIGINAL_MARKER = "# Original";
    public static final String LATEST_FORMAT_MARKER = "## Summary Format 2";

    @Inject
    InboxProperties inboxProperties;
    @Inject
    Now now;
    @Inject
    SlushyBoard slushyBoard;

    @Handler
    Submission reformat(
            @NonNull @Header(SlushyHeader.SUBMISSION) Submission submission,
            @NonNull @Header(SlushyHeader.CARD) TrelloCard card
    ) {
        // Name
        card.setName(String.format(
                "%s - %s by %s [%s] [%s] [%s]",
                card.getIdShort(),
                submission.getTitle(),
                submission.getByline(),
                submission.getGenre().getValue(),
                submission.getWindow().getValue(),
                submission.getWordLengthBand().getValue()
        ));
        // Due Date
        card.setDue(new Date(now.get()
                .plus(inboxProperties.dueDays(), ChronoUnit.DAYS)
                .atZone(ZoneOffset.ofHours(0)).toEpochSecond() * 1000));
        // Description/Body
        if (hasNoSummary(card) || hasOutdatedSummary(card)) {
            insertSummary(submission, card);
        }
        // Save
        slushyBoard.updateCard(card);

        return submission;
    }

    private boolean hasOutdatedSummary(TrelloCard card) {
        List<String> lines = cardDescLines(card).collect(Collectors.toList());
        return lines.stream()
                .noneMatch(LATEST_FORMAT_MARKER::equals);
    }

    private boolean hasNoSummary(TrelloCard card) {
        return cardDescLines(card)
                .noneMatch(ORIGINAL_MARKER::equals);
    }

    private Stream<String> cardDescLines(TrelloCard card) {
        return Arrays.stream(card.getDesc().split("\n"));
    }

    public Stream<String> originalDescLines(TrelloCard card) {
        if (cardDescLines(card).anyMatch(ORIGINAL_MARKER::equals)) {
            return cardDescLines(card)
                    .dropWhile(line -> !line.equals(ORIGINAL_MARKER))
                    .skip(1);
        }
        return cardDescLines(card);
    }

    private void insertSummary(Submission submission, TrelloCard card) {
        String summaryTemplate = String.join("\n", List.of(
                "> %s",
                "",
                "%s / %s",
                "> %s",
                "",
                "- email: %s",
                "- contract name: %s",
                "- paypal: %s",
                "",
                "---",
                LATEST_FORMAT_MARKER,
                ORIGINAL_MARKER,
                ""
        ));
        String summary = String.format(summaryTemplate,
                blockQuote(submission.getLogLine()),
                submission.getWordLengthBand().getValue(),
                submission.getGenre().getValue(),
                blockQuote(submission.getCoverLetter()),
                submission.getEmail(),
                submission.getRealName(),
                submission.getPaypal()
        );
        String original = originalDescLines(card)
                .collect(Collectors.joining("\n"));
        card.setDesc(summary + original);
    }

    private String blockQuote(String text) {
        String[] lines = text.split("\n");
        return String.join("\n> ", lines);
    }

}
