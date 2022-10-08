package net.kemitix.slushy.app.multisub;

import net.kemitix.slushy.app.*;
import net.kemitix.slushy.app.cardparsers.ParseSubmission;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IsMultipleSubmissionTest
        implements WithAssertions {

    @Mock
    DynamicMultiSubProperties config;

    @Mock
    ParseSubmission parser;

    @Mock
    SlushyBoard slushyBoard;

    @InjectMocks
    IsMultipleSubmission sut = new IsMultipleSubmission();

    Submission self = Submission.builder()
            .id("selfId")
            .title("selfTitle")
            .byline("selfByLine")
            .realName("selfReadName")
            .email("selfEmail")
            .paypal("selfPaypal")
            .wordLength(WordLengthBand.LENGTH_LONG_SHORT)
            .coverLetter("selfCoverLetter")
            .contract(Contract.ORIGINAL)
            .submittedDate(Instant.now())
            .document("selfDocument")
            .logLine("selfLogLine")
            .genre(Genre.Unknown)
            .window(Window.DIVERSITY)
            ;
    Submission other = Submission.builder()
            .id("otherId")
            .title("otherTitle")
            .byline("otherByLine")
            .realName("otherReadName")
            .email("otherEmail")
            .paypal("otherPaypal")
            .wordLength(WordLengthBand.LENGTH_LONG_SHORT)
            .coverLetter("otherCoverLetter")
            .contract(Contract.ORIGINAL)
            .submittedDate(Instant.now())
            .document("otherDocument")
            .logLine("selfLogLine")
            .genre(Genre.Unknown)
            .window(Window.GENERAL)
            ;

    Submission otherEmailToEmail = other.withEmail(self.getEmail());
    Submission otherEmailToPaypal = other.withEmail(self.getPaypal());
    Submission otherPaypalToEmail = other.withPaypal(self.getEmail());
    Submission otherPaypalToPaypal = other.withPaypal(self.getPaypal());

    @Mock
    TrelloCard selfCard;

    @Mock
    TrelloCard otherCard;

    List<TrelloCard> cardList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        given(config.lists()).willReturn("list");
        given(slushyBoard.getListCards("list")).willReturn(cardList);
    }

    @Test
    void doesNotMatchSelf() {
        //given
        given(parser.parse(selfCard)).willReturn(self);
        cardList.add(selfCard);
        //when
        RejectedMultipleSubmission result = sut.test(self);
        //then
        assertThat(result).isNull();
    }

    @Test
    void doesNotMatchOther() {
        //given
        given(parser.parse(otherCard)).willReturn(other);
        cardList.add(otherCard);
        //when
        RejectedMultipleSubmission result = sut.test(self);
        //then
        assertThat(result).isNull();
    }

    @Test
    void matchesDuplicateByEmailToEmail() {
        //given
        given(parser.parse(otherCard)).willReturn(otherEmailToEmail);
        cardList.add(otherCard);
        //when
        RejectedMultipleSubmission result = sut.test(self);
        //then
        assertThat(result.getCurrent()).isEqualTo(self);
        assertThat(result.getExisting()).isEqualTo(otherEmailToEmail);
    }

    @Test
    void matchesDuplicateByEmailToPaypal() {
        //given
        given(parser.parse(otherCard)).willReturn(otherEmailToPaypal);
        cardList.add(otherCard);
        //when
        RejectedMultipleSubmission result = sut.test(self);
        //then
        assertThat(result.getCurrent()).isEqualTo(self);
        assertThat(result.getExisting()).isEqualTo(otherEmailToPaypal);
    }

    @Test
    void matchesDuplicateByPaypalToEmail() {
        //given
        given(parser.parse(otherCard)).willReturn(otherPaypalToEmail);
        cardList.add(otherCard);
        //when
        RejectedMultipleSubmission result = sut.test(self);
        //then
        assertThat(result.getCurrent()).isEqualTo(self);
        assertThat(result.getExisting()).isEqualTo(otherPaypalToEmail);
    }

    @Test
    void matchesDuplicateByPaypalToPaypal() {
        //given
        given(parser.parse(otherCard)).willReturn(otherPaypalToPaypal);
        cardList.add(otherCard);
        //when
        RejectedMultipleSubmission result = sut.test(self);
        //then
        assertThat(result.getCurrent()).isEqualTo(self);
        assertThat(result.getExisting()).isEqualTo(otherPaypalToPaypal);
    }

}
