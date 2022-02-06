package net.kemitix.slushy.app.reader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.kemitix.slushy.app.fileconversion.ConvertAttachment;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class FormatForReaderRoute
        extends RouteBuilder {

    @Inject
    ConvertAttachment convertAttachment;

    @Override
    public void configure() {
        from("direct:Slushy.FormatForReader")
                .routeId("Slushy.FormatForReader")
                .setHeader("SlushyReadableAttachment").method(convertAttachment)
        ;

    }
}
