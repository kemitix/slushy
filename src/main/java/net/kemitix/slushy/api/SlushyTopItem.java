package net.kemitix.slushy.api;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.cardparsers.ParseSubmission;
import net.kemitix.slushy.app.reader.DynamicReaderProperties;
import net.kemitix.slushy.trello.SlushyBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class SlushyTopItem {

    @Inject
    private SlushyBoard slushyBoard;
    @Inject
    private DynamicReaderProperties readerProperties;
    @Inject
    private ParseSubmission parseSubmission;

    public List<Submission> topItem() {
        return slushyBoard.getListCards(readerProperties.targetList())
                .stream()
                .findFirst()
                .map(parseSubmission::parse)
                .stream()
                .collect(Collectors.toList());
    }

}
