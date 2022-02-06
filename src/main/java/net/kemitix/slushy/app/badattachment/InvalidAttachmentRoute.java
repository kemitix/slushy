package net.kemitix.slushy.app.badattachment;

import net.kemitix.slushy.app.ValidFileTypes;
import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class InvalidAttachmentRoute
        extends RouteBuilder {

    @Inject ValidFileTypes validFileTypes;

    @Override
    public void configure() {
        from("direct:Slushy.InvalidAttachment")
                .routeId("Slushy.InvalidAttachment")
                .log("Submission rejected due to an unsupported file type")

                .setHeader("SlushyValidFileTypes").constant(validTypes())

                .setHeader("SlushyEmailTemplate").constant("badattachment")
                .to("direct:Slushy.SendEmail")

                // move card to rejected
                .to("direct:Slushy.Reject.MoveToTargetList")
        ;
    }

    private String validTypes() {
        return String.join(", ", validFileTypes.get());
    }

}
