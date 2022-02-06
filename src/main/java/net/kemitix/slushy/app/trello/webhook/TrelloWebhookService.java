package net.kemitix.slushy.app.trello.webhook;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

@Path("/1")
@RegisterRestClient(configKey = "trello")
public interface TrelloWebhookService {

    @POST
    @Path("/tokens/{token}/webhooks/")
    @Consumes(MediaType.APPLICATION_JSON)
    Response registerWebhook(
            @PathParam("token") String token,
            @QueryParam("key") String key,
            TrelloCallback trelloCallback);

}
