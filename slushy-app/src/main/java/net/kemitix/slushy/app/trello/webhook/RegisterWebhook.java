package net.kemitix.slushy.app.trello.webhook;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.SlushyConfig;
import net.kemitix.slushy.app.trello.TrelloBoard;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Log
@ApplicationScoped
public class RegisterWebhook {

    private final SlushyConfig slushyConfig;
    private final TrelloBoard trelloBoard;
    @Inject
    @RestClient
    private TrelloWebhookService trelloWebhookService;

    @Inject
    public RegisterWebhook(
            SlushyConfig slushyConfig,
            TrelloBoard trelloBoard
    ) {
        this.slushyConfig = slushyConfig;
        this.trelloBoard = trelloBoard;
    }

    //$.post("https://api.trello.com/1/tokens/{APIToken}/webhooks/?key={APIKey}", {
    //  description: "My first webhook",
    //  callbackURL: "http://www.mywebsite.com/trelloCallback",
    //  idModel: "4d5ea62fd76aa1136000000c",
    //});

    //TODO https://quarkus.io/guides/rest-client
    public void register() {
        log.info("init()");

        String apiKey = slushyConfig.getTrelloKey();
        String apiToken = slushyConfig.getTrelloSecret();
        String boardId = trelloBoard.getBoardId();
        String callbackUrl = String.format(
                "%s?source=trello;board=%s",
                slushyConfig.getWebhook(),
                boardId);

        String description = "Slushy Webhook";
        TrelloCallback callback = TrelloCallback.builder()
                .description(description)
                .callbackURL(callbackUrl)
                .idModel(boardId)
                .build();
        log.info(callback.toString());
        try {
            Response response = trelloWebhookService.registerWebhook(apiToken, apiKey, callback);
            log.info(String.format(
                    "Webhook Registered: (%d) %s - %s",
                    response.getStatus(),
                    response.getStatusInfo().getReasonPhrase(),
                    response.getEntity()
                    ));
        } catch (WebApplicationException e) {
            String message = e.getResponse().readEntity(String.class);
            if ("A webhook with that callback, model, and token already exists".equals(message)) {
                log.info(message);
            } else {
                throw new RuntimeException(
                        "Unable to register webhook with Trello: " + message, e);
            }
        }
    }

}