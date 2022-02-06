package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SlushyCardConverter implements TypeConverters {

    @Inject Trello trello;

    @Converter
    public TrelloCard toSlushyCard(Card card) {
        return TrelloCard.from(card, trello);
    }

}
