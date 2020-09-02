package net.kemitix.slushy.app.trello;

import com.julienvey.trello.TrelloHttpClient;
import com.julienvey.trello.impl.TrelloImpl;

public class SlushyTrelloClient
        extends TrelloImpl
        implements TrelloClient{

    public SlushyTrelloClient(
            String applicationKey,
            String accessToken,
            TrelloHttpClient httpClient
    ) {
        super(applicationKey, accessToken, httpClient);
    }

}
