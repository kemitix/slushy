package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@Log
@ApplicationScoped
public class TrelloProducers {

    @Produces
    Trello trello() {
        String key = System.getenv("TRELLO_KEY");
        String secret = System.getenv("TRELLO_SECRET");
        return new TrelloImpl(key, secret, new ApacheHttpClient());
    }

}
