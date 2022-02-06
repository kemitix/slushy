package net.kemitix.slushy.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.kemitix.slushy.app.Submission;

import jakarta.inject.Inject;
import java.util.List;

@Path("/slush/topitem")
public class SlushyTopItemResource {

    @Inject
    private SlushyTopItem slushyTopItem;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Submission> get() {
        return slushyTopItem.topItem();
    }

}
