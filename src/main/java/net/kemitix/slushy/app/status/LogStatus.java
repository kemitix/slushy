package net.kemitix.slushy.app.status;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.ErrorHolder;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Log
@ApplicationScoped
public class LogStatus {

    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
                    .withLocale(Locale.UK)
                    .withZone(ZoneId.of("Europe/London"));
    @Inject
    SlushyBoard slushyBoard;
    @Inject
    DynamicStatusProperties statusProperties;
    @Inject
    ErrorHolder errorHolder;

    void status() {
        noteAcknowledgedErrors();
        log.info("Updating status card");
        List<String> status = new ArrayList<>();
        String dateTime = DATE_TIME_FORMATTER.format(Instant.now());
        status.add("Last updated: " + dateTime);
        status.add("---");
        slushyBoard.getListNames()
                .forEach(listName ->
                        status.add(String.format("%4d: %s\n",
                                slushyBoard.getListCards(listName).size(),
                                listName)));
        final List<ErrorHolder.Error> errors = errorHolder.errors();
        status.add("Errors: " + errors.size());
        errors.stream().map(e -> String.format("- [ ] :%d: %s", e.id, e.message))
                .forEach(status::add);
        updateStatusCard(String.join("\n", status));
    }

    private void noteAcknowledgedErrors() {
        findStatusCard()
                .map(TrelloCard::getDesc)
                .ifPresent(card -> {
                    card.lines()
                            .filter(line -> line.startsWith("- [x]"))
                            .map(line -> line.split(":")[1])
                            .map(Integer::parseInt)
                            .forEach(id -> errorHolder.acknowledge(id));
                });
    }

    private void updateStatusCard(String message) {
        findStatusCard()
                .ifPresent(statusCard -> {
                    statusCard.setDesc(message);
                    statusCard.update();
                });
    }

    private Optional<TrelloCard> findStatusCard() {
        return slushyBoard.getListCards(statusProperties.listName())
                .stream()
                .filter(card -> statusProperties.cardName().equals(card.getName()))
                .findFirst();
    }
}
