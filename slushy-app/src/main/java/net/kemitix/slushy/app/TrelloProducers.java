package net.kemitix.slushy.app;

import lombok.extern.java.Log;
import net.kemitix.ugiggle.trello.TrelloConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@Log
@ApplicationScoped
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

}
