package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;

public class UnknownCardFormatException
        extends RuntimeException {
    private final Card card;

    public UnknownCardFormatException(Card card) {
        this.card = card;
    }

    @Override
    public String getMessage() {
        return String.format("Unrecognised card format %s: %s%n%s",
                card.getIdShort(), card.getName(), card.getDesc());
    }
}
