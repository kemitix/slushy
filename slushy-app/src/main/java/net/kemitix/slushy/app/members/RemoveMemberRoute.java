package net.kemitix.slushy.app.members;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class RemoveMemberRoute
        extends RouteBuilder {

    @Inject RemoveMember removeMember;

    @Override
    public void configure() {
        from("direct:Slushy.RemoveMember")
                .routeId("Slushy.RemoveMember")
                .setHeader("SlushyCard", bean(removeMember))
        ;
    }

}
