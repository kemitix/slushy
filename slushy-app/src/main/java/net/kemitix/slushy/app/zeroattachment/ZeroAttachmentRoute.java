package net.kemitix.slushy.app.zeroattachment;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ZeroAttachmentRoute
        extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:Slushy.ZeroAttachment")
                .routeId("Slushy.ZeroAttachment")
                .log("zero attachment")
                .setHeader("SlushyEmailTemplate")
                .constant("zeroattachment")
                .log("sending email")
                .to("direct:Slushy.SendEmail")
                .log("Email sent")
                // move card to rejected
                .to("direct:Slushy.Reject.MoveToTargetList")
        ;
    }
}
