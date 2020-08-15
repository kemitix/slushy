package net.kemitix.slushy.app.ugiggle;

import com.julienvey.trello.Trello;
import com.julienvey.trello.TrelloHttpClient;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@Log
//@ApplicationScoped
public class TrelloProducers {

    @Produces
    TrelloConfig trelloConfig() {
        return new TrelloConfig() {
            @Override
            public String getTrelloKey() {
                return System.getenv("TRELLO_KEY");
            }

            @Override
            public String getTrelloAccessToken() {
                return System.getenv("TRELLO_SECRET");
            }
        };
    }

    @Produces
    @ApplicationScoped
    TrelloHttpClient trelloHttpClient() {
        return new JDKTrelloHttpClient();
    }

    @Produces
    @ApplicationScoped
    Trello trello(
            TrelloConfig config,
            TrelloHttpClient httpClient
    ) {
        return new TrelloImpl(
                config.getTrelloKey(),
                config.getTrelloAccessToken(),
                httpClient);
    }

}
