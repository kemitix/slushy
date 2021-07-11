package net.kemitix.slushy.app.status;

import lombok.extern.java.Log;
import net.kemitix.trello.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Log
@ApplicationScoped
public class LogStatus {

    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
                    .withLocale(Locale.UK)
                    .withZone(ZoneId.systemDefault());
    @Inject TrelloBoard trelloBoard;
    @Inject StatusProperties statusProperties;

    void status() {
        log.info("Updating status card");
        List<String> status = new ArrayList<>();
        String dateTime = DATE_TIME_FORMATTER.format(Instant.now());
        status.add("Last updated: " + dateTime);
        status.add("---");
        trelloBoard.getListNames()
                .forEach(listName ->
                        status.add(String.format("%4d: %s\n",
                                trelloBoard.getListCards(listName).size(),
                                listName)));
        updateStatusCard(String.join("\n", status));
    }

    private void updateStatusCard(String message) {
        trelloBoard.getListCards(statusProperties.listName())
                .stream()
                .filter(card -> statusProperties.cardName().equals(card.getName()))
                .findFirst()
                .ifPresent(statusCard -> {
                    statusCard.setDesc(message);
                    statusCard.update();
                });
    }
}
