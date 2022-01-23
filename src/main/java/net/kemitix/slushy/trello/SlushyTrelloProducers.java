package net.kemitix.slushy.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.TrelloHttpClient;
import com.julienvey.trello.domain.Member;
import net.kemitix.trello.AttachmentDirectory;
import net.kemitix.trello.AttachmentDirectoryImpl;
import net.kemitix.trello.TrelloConfig;
import net.kemitix.trello.TrelloProducers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class SlushyTrelloProducers {

    private final TrelloProducers trelloProducers = new TrelloProducers();

    @Produces
    @ApplicationScoped
    Trello trello(
            TrelloConfig trelloConfig,
            TrelloHttpClient trelloHttpClient
    ) {
        return trelloProducers.trello(
                trelloConfig, trelloHttpClient
        );
    }

    @Produces
    @ApplicationScoped
    TrelloHttpClient trelloHttpClient() {
        return trelloProducers.trelloHttpClient();
    }

    @Produces
    @ApplicationScoped
    Member member(
            Trello trello,
            TrelloConfig trelloConfig
    ) {
        return trelloProducers.member(trello, trelloConfig);
    }

    @Produces
    @ApplicationScoped
    AttachmentDirectory attachmentDirectory() {
        return new AttachmentDirectoryImpl();
    }
}
