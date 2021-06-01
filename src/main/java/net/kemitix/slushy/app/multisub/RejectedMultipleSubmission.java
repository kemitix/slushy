package net.kemitix.slushy.app.multisub;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kemitix.slushy.app.Submission;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RejectedMultipleSubmission {

    private Submission current;
    private Submission existing;

    public static Builder builder() {
        return current -> existing
                -> new RejectedMultipleSubmission(current, existing);
    }

    public interface Builder {
        Stage1 current(Submission current);
        interface Stage1 {RejectedMultipleSubmission existing(Submission existing);}
    }

}
