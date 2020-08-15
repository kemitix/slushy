package net.kemitix.slushy.app.ugiggle;

import java.util.stream.Stream;

public interface ReadingListService {
    Stream<TrelloCard> getReadingList();
}
