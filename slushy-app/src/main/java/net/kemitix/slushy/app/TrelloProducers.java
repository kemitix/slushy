package net.kemitix.slushy.app;

import com.julienvey.trello.Trello;
import com.julienvey.trello.TrelloHttpClient;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import lombok.extern.java.Log;
import net.kemitix.slushy.spi.SlushyConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class TrelloProducers {

    @Produces
    @ApplicationScoped
    TrelloHttpClient trelloHttpClient() {
        return new JDKTrelloHttpClient();
    }

    @Produces
    @ApplicationScoped
    Trello trello(
            SlushyConfig config,
            TrelloHttpClient httpClient
    ) {
        return new TrelloImpl(
                config.getTrelloKey(),
                config.getTrelloSecret(),
                httpClient);
    }

}