package net.kemitix.slushy.app.reader;

import net.kemitix.trello.TrelloCard;
import net.kemitix.trello.TrelloBoard;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ReaderIsFullTest
        implements WithAssertions {

    @Mock ReaderConfig readerConfig;
    @Mock TrelloBoard trelloBoard;

    @InjectMocks ReaderIsFull readerIsFull;

    @Mock List<TrelloCard> targetList ;

    @Test
    public void ShouldNotBeFullWhenListSizeIsBelowMax() {
        //given
        givenMaxListSize(2);
        givenListSize(1);
        //when
        boolean test = readerIsFull.test();
        //then
        assertThat(test).isFalse();
    }

    private void givenListSize(int listSize) {
        String listName = "list-name";
        given(readerConfig.getTargetList()).willReturn(listName);
        given(trelloBoard.getListCards(listName)).willReturn(targetList);
        given(targetList.size()).willReturn(listSize);
    }

    private void givenMaxListSize(int maxListSize) {
        given(readerConfig.getMaxSize()).willReturn(maxListSize);
    }

    @Test
    public void ShouldNotBeFullWhenMaxIsNegOne() {
        //given
        givenMaxListSize(-1);
        //when
        boolean test = readerIsFull.test();
        //then
        assertThat(test).isFalse();
    }

    @Test
    public void ShouldBeFullWhenListSizeIsAtMax() {
        //given
        givenMaxListSize(2);
        givenListSize(2);
        //when
        boolean test = readerIsFull.test();
        //then
        assertThat(test).isTrue();
    }

    @Test
    public void ShouldBeFullWhenListSizeIsAboveMax() {
        //given
        givenMaxListSize(2);
        givenListSize(3);
        //when
        boolean test = readerIsFull.test();
        //then
        assertThat(test).isTrue();
    }
}