package net.kemitix.slushy.app.ugiggle;

import net.kemitix.slushy.app.trello.TrelloCard;

import java.util.stream.Stream;

public interface ReadingListService {
    Stream<TrelloCard> getReadingList();
}
