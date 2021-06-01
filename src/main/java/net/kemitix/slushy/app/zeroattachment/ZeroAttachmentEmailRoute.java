package net.kemitix.slushy.app.zeroattachment;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ZeroAttachmentEmailRoute
        extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:Slushy.ZeroAttachment.Email")
                .routeId("Slushy.ZeroAttachment.Email")
                .setHeader("SlushyEmailTemplate").constant("zeroattachment")
                .to("direct:Slushy.SendEmail")
        ;
    }
}
