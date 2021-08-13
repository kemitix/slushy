package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.AttachmentLoader;
import net.kemitix.slushy.app.TrelloLoginPageException;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LoadAttachmentRoute
        extends RouteBuilder {
    @Inject AttachmentLoader attachmentLoader;
    @Override
    public void configure() {

        onException(TrelloLoginPageException.class)
                .to("direct:Slushy.Status.Update")
        ;

        from("direct:Slushy.LoadAttachment")
                .routeId("Slushy.LoadAttachment")

                .setHeader("SlushyAttachment").method(attachmentLoader)

                .choice()
                .when(simple("${header.SlushyAttachment.isZero}"))
                .log("Attachment is empty")
                .to("direct:Slushy.ZeroAttachment")
                .process(exchange -> exchange.setRouteStop(true))
                .end()
        ;

    }
}
