package net.kemitix.slushy.app;

import com.julienvey.trello.domain.Card;
import lombok.extern.java.Log;
import org.apache.camel.Body;

import javax.enterprise.context.ApplicationScoped;

@Log
@ApplicationScoped
public class ValidateSubmission {

    Card validate(@Body Card card) {
        log.info("CARD " + card.getName());
        //TODO
        return card;
    }
}
