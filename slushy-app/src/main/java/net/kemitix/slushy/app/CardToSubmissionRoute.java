package net.kemitix.slushy.app;

import net.kemitix.slushy.app.cardparsers.ParseSubmission;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
                .setHeader("SlushyCard").body()
                .setHeader("SlushySubmission").method(parseSubmission)
        ;
    }
}
