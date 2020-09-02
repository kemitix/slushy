package net.kemitix.slushy.app.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.TrelloHttpClient;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
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

    @Produces
    @ApplicationScoped
    Member member(Trello trello, SlushyConfig slushyConfig) {
        return trello.getMemberInformation(slushyConfig.getUserName());
    }

}
