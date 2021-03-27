package net.kemitix.slushy.app;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

import java.time.Instant;

@Getter
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Submission {

    private final String id;
    private final String title;
    private final String byline;
    private final String realName;
    private final String email;
    private final String paypal;
    private final WordLengthBand wordLengthBand;
    private final String coverLetter;
    private final Contract contract;
    private final Instant date;
    private final String document;
    private final String logLine;
    private final Genre genre;

    public static Builder builder() {
        return id -> title -> byline -> realName -> email -> paypal
                -> wordLengthBand -> coverLetter -> contract -> date
                -> document -> logLine -> genre
                -> new Submission(id, title, byline, realName, email, paypal,
                wordLengthBand, coverLetter, contract, date, document, logLine,
                genre);
    }

    public boolean hasDocument() {
        return document != null;
    }

    public interface Builder {
        Stage0 id(String id);
        interface Stage0 {Stage1 title(String title);}
        interface Stage1 {Stage2 byline(String byline);}
        interface Stage2 {Stage3 realName(String realName);}
        interface Stage3 {Stage4 email(String email);}
        interface Stage4 {Stage5 paypal(String paypal);}
        interface Stage5 {Stage6 wordLength(WordLengthBand wordLength);}
        interface Stage6 {Stage7 coverLetter(String coverLetter);}
        interface Stage7 {Stage8 contract(Contract contract);}
        interface Stage8 {Stage9 submittedDate(Instant submittedDate);}
        interface Stage9 {Stage10 document(String document);}
        interface Stage10 {Stage11 logLine(String logLine);}
        interface Stage11 {Submission genre(Genre genre);}
    }
}
