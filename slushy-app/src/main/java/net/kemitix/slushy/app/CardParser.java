package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;

import java.util.Map;

public interface CardParser {
    boolean canHandle(Card card);

    Map<String, String> parse(Card card);
}
