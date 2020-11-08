package net.kemitix.slushy.app.status;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.ListProcessConfig;
import net.kemitix.slushy.app.trello.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Log
@ApplicationScoped
public class LogStatus {

    @Inject Instance<ListProcessConfig> listProcessConfigs;
    @Inject TrelloBoard trelloBoard;

    void status() {
        List<String> status = new ArrayList<>();
        status.add("Status:");
        trelloBoard.getListNames()
                .forEach(listName ->
                        status.add(String.format("%4d: %s",
                                trelloBoard.getListCards(listName).size(),
                                listName)));
        log.info(String.join("\n", status));
    }
}
