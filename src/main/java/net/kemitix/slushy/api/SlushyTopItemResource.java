package net.kemitix.slushy.api;

import net.kemitix.slushy.app.Submission;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
