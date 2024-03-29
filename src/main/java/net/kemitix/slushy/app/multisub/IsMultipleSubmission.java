package net.kemitix.slushy.app.multisub;

import lombok.NonNull;
import net.kemitix.slushy.app.SlushyHeader;
import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.cardparsers.ParseSubmission;
import net.kemitix.slushy.trello.SlushyBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

@ApplicationScoped
public class IsMultipleSubmission {

    @Inject
    DynamicMultiSubProperties multiSubProperties;
    @Inject
    SlushyBoard slushyBoard;
    @Inject
    ParseSubmission parseSubmission;

    // if email or paypal in any of slush, reject, hold, held
    @Handler
    RejectedMultipleSubmission test(
            @NonNull @Header(SlushyHeader.SUBMISSION) Submission submission
    ) {
        var matchSubmission = subComparator(submission);
        return Arrays.stream(multiSubProperties.lists().split(","))
                .map(String::trim)
                .map(slushyBoard::getListCards)
                .flatMap(Collection::stream)
                .map(parseSubmission::parse)
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
        String submissionId = submission.getId();
        return (Submission other) ->
                !other.getId().equals(submissionId) &&
                        (other.getEmail().equals(email) ||
                                other.getEmail().equals(paypal) ||
                                other.getPaypal().equals(email) ||
                                other.getPaypal().equals(paypal));
    }

}
