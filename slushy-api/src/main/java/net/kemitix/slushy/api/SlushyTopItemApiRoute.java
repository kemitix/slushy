package net.kemitix.slushy.api;

import lombok.extern.java.Log;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@Log
@ApplicationScoped
public class SlushyTopItemApiRoute
        extends RouteBuilder {

    @Override
    public void configure() {
        from("platform-http:/slush/topitem")
                .routeId("Slushy.TopItem")
                .log("slush-top-item")
                .setBody(constant("TOP ITEM"))
        ;
    }
}