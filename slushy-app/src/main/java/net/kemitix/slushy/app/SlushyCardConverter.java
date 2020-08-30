package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;

@ApplicationScoped
public class SlushyCardConverter implements TypeConverters {

    @Inject Trello trello;

    @Converter
    public SlushyCard toSlushyCard(Card card) {
//        System.out.println("trello = " + trello);
//        Objects.requireNonNull(trello, "Trello Service");
        return SlushyCard.from(card, trello);
    }

}
