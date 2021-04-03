package net.kemitix.slushy.app.multisub;

import com.sun.istack.Nullable;
import lombok.NonNull;
import net.kemitix.slushy.app.SlushyHeader;
import net.kemitix.slushy.app.Submission;
import net.kemitix.slushy.app.cardparsers.ParseSubmission;
import net.kemitix.trello.TrelloBoard;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

@ApplicationScoped
public class IsMultipleSubmission {

    @Inject MultiSubConfig multiSubConfig;
    @Inject TrelloBoard trelloBoard;
    @Inject ParseSubmission parseSubmission;

    // if email or paypal in any of slush, reject, hold, held
    @Handler
    @Nullable
    RejectedMultipleSubmission test(
            @NonNull @Header(SlushyHeader.SUBMISSION) Submission submission
    ) {
        var matchSubmission = subComparator(submission);
        return Arrays.stream(multiSubConfig.getLists().split(","))
                .map(String::trim)
                .map(trelloBoard::getListCards)
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
