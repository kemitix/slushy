package net.kemitix.slushy.app;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Submission {

    private final String title;
    private final String byline;
    private final String realName;
    final String email;
    private final String paypal;
    private final WordLengthBand wordLengthBand;
    private final String coverLetter;
    private final Contract contract;
    private final Instant date;
    private final String document;

    public static Builder builder() {
        return title -> byline -> realName -> email -> paypal
                -> wordLengthBand -> coverLetter -> contract -> date
                -> document
                -> new Submission(title, byline, realName, email, paypal,
                wordLengthBand, coverLetter, contract, date, document);
    }

    public boolean isValid() {
        return document != null;
    }

    public interface Builder {
        Stage1 title(String title);
        interface Stage1 {Stage2 byline(String byline);}
        interface Stage2 {Stage3 realName(String realName);}
        interface Stage3 {Stage4 email(String email);}
        interface Stage4 {Stage5 paypal(String paypal);}
        interface Stage5 {Stage6 wordLength(WordLengthBand wordLength);}
        interface Stage6 {Stage7 coverLetter(String coverLetter);}
        interface Stage7 {Stage8 contract(Contract contract);}
        interface Stage8 {Stage9 submittedDate(Instant submittedDate);}
        interface Stage9 {Submission document(String document);}
    }
}
