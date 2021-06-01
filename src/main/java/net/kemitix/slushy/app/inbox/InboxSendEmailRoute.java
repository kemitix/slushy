package net.kemitix.slushy.app.inbox;

import com.julienvey.trello.domain.Action;
import com.julienvey.trello.domain.Argument;
import net.kemitix.slushy.app.SlushyHeader;
import net.kemitix.trello.TrelloCard;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InboxSendEmailRoute
        extends RouteBuilder {

    private static final Argument FILTER_COMMENTS =
            new Argument("filter", "commentCard");
    private static final String EMAIL_SENT_SIGNATURE = "Sent inbox email to author";

    @Override
    public void configure() {
        from("direct:Slushy.Inbox.SendEmailConfirmation")
                .routeId("Slushy.Inbox.SendEmailConfirmation")
                .filter(excludeWhenConfirmationAlreadySent())
                .setHeader("SlushyEmailTemplate").constant("inbox")
                .to("direct:Slushy.SendEmail")
        ;
    }

    private Predicate excludeWhenConfirmationAlreadySent() {
        return exchange -> exchange.getIn()
                .getHeader(SlushyHeader.CARD, TrelloCard.class)
                .getActions(FILTER_COMMENTS)
                .stream()
                .map(Action::getData)
                .map(Action.Data::getText)
                .noneMatch(EMAIL_SENT_SIGNATURE::equals);
    }
}
