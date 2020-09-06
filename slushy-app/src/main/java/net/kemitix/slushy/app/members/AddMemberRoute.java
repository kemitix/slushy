package net.kemitix.slushy.app.members;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AddMemberRoute
        extends RouteBuilder {

    @Inject AddMember addMember;

    @Override
    public void configure() {
        from("direct:Slushy.AddMember")
                .routeId("Slushy.AddMember")
                .setHeader("SlushyCard").method(addMember)
        ;
    }

}
