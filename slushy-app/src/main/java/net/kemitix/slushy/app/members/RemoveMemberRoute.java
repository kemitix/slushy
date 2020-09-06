package net.kemitix.slushy.app.members;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RemoveMemberRoute
        extends RouteBuilder {

    @Inject RemoveMember removeMember;

    @Override
    public void configure() {
        from("direct:Slushy.RemoveMember")
                .routeId("Slushy.RemoveMember")
                .setHeader("SlushyCard").method(removeMember)
        ;
    }

}
