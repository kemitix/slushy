package net.kemitix.slushy.app.multisub;

import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.SubmissionParser;
import net.kemitix.slushy.app.TrelloBoard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

@ApplicationScoped
public class MultiSubmission {

    @Inject
    TrelloBoard trelloBoard;
    @Inject
    SubmissionParser parser;

    // if email or paypal in any of slush, reject, hold, held
    RejectedMultipleSubmission test(
            Submission submission
    ) {
        var matchSubmission = subComparator(submission);
        return Stream.of(
                trelloBoard.getSlushCards(),
                trelloBoard.getRejectCards(),
                trelloBoard.getHoldCards(),
                trelloBoard.getHeldCards()
        )
                .flatMap(Collection::stream)
                .map(parser::parse)
                .filter(matchSubmission::apply)
                .findFirst()
                .map(other -> rejection(submission, other))
                .orElse(null);
    }

    private RejectedMultipleSubmission rejection(
            Submission submission,
            Submission other
    ) {
        return RejectedMultipleSubmission.builder()
                .current(submission)
                .existing(other);
    }

    private Function<Submission, Boolean> subComparator(
            Submission submission
    ) {
        String email = submission.getEmail();
        String paypal = submission.getPaypal();
        return (Submission other) ->
                other.getEmail().equals(email) ||
                        other.getEmail().equals(paypal) ||
                        other.getPaypal().equals(email) ||
                        other.getPaypal().equals(paypal);
    }

}