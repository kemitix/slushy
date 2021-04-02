package net.kemitix.slushy.app.cardparsers;

import com.julienvey.trello.domain.Card;
import net.kemitix.slushy.app.CardBodyCleaner;

import java.util.Map;

public interface CardParser {
    boolean canHandle(Card card);

    Map<String, String> parse(Card card);

    void setCardBodyCleaner(CardBodyCleaner cardBodyCleaner);
}
