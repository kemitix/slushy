package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.fileconversion.ConvertAttachment;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FormatForReaderRoute
        extends RouteBuilder {

    @Inject ConvertAttachment convertAttachment;

    @Override
    public void configure() throws Exception {
        from("direct:Slushy.FormatForReader")
                .routeId("Slushy.FormatForReader")
                .setHeader("SlushyReadableAttachment").method(convertAttachment)
        ;

    }
}
