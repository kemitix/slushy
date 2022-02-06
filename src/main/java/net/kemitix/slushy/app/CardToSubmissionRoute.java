package net.kemitix.slushy.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.kemitix.slushy.app.cardparsers.ParseSubmission;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class CardToSubmissionRoute
        extends RouteBuilder {
    private final ParseSubmission parseSubmission;

    @Inject
    public CardToSubmissionRoute(ParseSubmission parseSubmission) {
        this.parseSubmission = parseSubmission;
    }

    @Override
    public void configure() {
        from("direct:Slushy.CardToSubmission")
                .routeId("Slushy.CardToSubmission")
                .setHeader(SlushyHeader.CARD).body()
                .setHeader(SlushyHeader.SUBMISSION).method(parseSubmission)
        ;
    }
}
