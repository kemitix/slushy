package net.kemitix.slushy.app.status;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.ListProcessConfig;
import net.kemitix.slushy.app.trello.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.stream.Stream;

@Log
@ApplicationScoped
public class LogStatus {

    @Inject Instance<ListProcessConfig> listProcessConfigs;
    @Inject TrelloBoard trelloBoard;

    void status() {
        log.info("Status:");
        listProcessConfigs.stream()
                .flatMap(config -> Stream.of(config.getSourceList(), config.getTargetList()))
                .distinct()
                .forEach(listName ->
                        log.info(String.format("%4d: %s",
                                trelloBoard.getListCards(listName).size(),
                                listName)));
    }
}
