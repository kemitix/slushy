package net.kemitix.slushy.api;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.cardparsers.ParseSubmission;
import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.reader.ReaderConfig;
import net.kemitix.trello.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class SlushyTopItem {

    private final TrelloBoard trelloBoard;
    private final ReaderConfig readerConfig;
    private final ParseSubmission parseSubmission;

    @Inject
    public SlushyTopItem(
            TrelloBoard trelloBoard,
            ReaderConfig readerConfig,
            ParseSubmission parseSubmission
    ) {
        this.trelloBoard = trelloBoard;
        this.readerConfig = readerConfig;
        this.parseSubmission = parseSubmission;
    }

    public List<Submission> topItem() {
        return trelloBoard.getListCards(readerConfig.getTargetList())
                .stream()
                .findFirst()
                .map(parseSubmission::parse)
                .stream()
                .collect(Collectors.toList());
    }

}
