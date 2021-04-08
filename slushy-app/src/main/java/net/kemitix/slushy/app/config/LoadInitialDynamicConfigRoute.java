package net.kemitix.slushy.app.config;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LoadInitialDynamicConfigRoute
        extends RouteBuilder {

    @Inject LoadDynamicConfig loadDynamicConfig;

    @Override
    public void configure() throws Exception {
        from("timer:load-initial-dynamic-config?repeatCount=1")
                .routeId("load-initial-dynamic-config")
                .to("direct:Slushy.Dynamic.Config.Update")
        ;

        from("direct:Slushy.Dynamic.Config.Update")
                .routeId("Slushy.Dynamic.Config.Update")
                .bean(loadDynamicConfig)
        ;
    }
}
