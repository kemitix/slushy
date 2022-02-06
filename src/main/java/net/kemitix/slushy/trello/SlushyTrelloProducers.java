package net.kemitix.slushy.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.TrelloHttpClient;
import com.julienvey.trello.domain.Member;
import net.kemitix.trello.AttachmentDirectory;
import net.kemitix.trello.AttachmentDirectoryImpl;
import net.kemitix.trello.TrelloConfig;
import net.kemitix.trello.TrelloProducers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.io.IOException;

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
    AttachmentDirectory attachmentDirectory() throws IOException {
        final AttachmentDirectoryImpl attachmentDirectory = new AttachmentDirectoryImpl();
        attachmentDirectory.init();
        return attachmentDirectory;
    }
}
