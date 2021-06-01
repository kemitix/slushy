package net.kemitix.slushy.app.trello.webhook;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
