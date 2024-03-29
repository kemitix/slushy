package net.kemitix.slushy.app.members;

import net.kemitix.slushy.app.SlushyHeader;
import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RemoveMemberRoute
        extends RouteBuilder {

    @Inject RemoveMember removeMember;

    @Override
    public void configure() {
        from("direct:Slushy.RemoveMember")
                .routeId("Slushy.RemoveMember")
                .setHeader(SlushyHeader.CARD).method(removeMember)
        ;
    }

}
