package net.kemitix.slushy.app.zeroattachment;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

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
