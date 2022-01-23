package net.kemitix.slushy.app;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ErrorHolderTest
        implements WithAssertions {

    @Test
    void addedErrorsHaveIncrementingId() {
        //given
        final ErrorHolder errorHolder = defaultErrorHolder();

        //when
        final ErrorHolder.Error error1 = errorHolder.add("error 1");
        final ErrorHolder.Error error2 = errorHolder.add("error 2");

        //then
        assertThat(error1.id).isEqualTo(1);
        assertThat(error2.id).isEqualTo(2);
    }

    @Test
    void addAndGet() {
        //given
        final ErrorHolder errorHolder = defaultErrorHolder();

        //when
        final ErrorHolder.Error error = errorHolder.add("message");
        final List<ErrorHolder.Error> errors = errorHolder.errors();

        //then
        assertThat(errors).containsExactly(error);
    }

    @Test
    void acknowledgedErrorIsGone() {
        //given
        final ErrorHolder errorHolder = defaultErrorHolder();
        final ErrorHolder.Error error = errorHolder.add("message");

        //when
        errorHolder.acknowledge(error.id);

        //then
        assertThat(errorHolder.errors()).isEmpty();
    }

    @Test
    void addOverMaxErrors() {
        //given
        final ErrorHolder errorHolder = defaultErrorHolder();
        for (int i = 1; i <= 12; i++) {
            errorHolder.add("#" + i);
        }

        //when
        final List<ErrorHolder.Error> errors = errorHolder.errors();

        //then
        assertThat(errors).hasSize(10)
                .extracting(e -> e.message)
                .containsExactly(
                        "#3", "#4", "#5", "#6", "#7",
                        "#8", "#9", "#10", "#11", "#12");
    }

    @Test
    void messagesExpire() {
        //given
        Now now = mock(Now.class);
        final ErrorHolder errorHolder = new ErrorHolder();
        errorHolder.now = now;
        given(now.get())
                .willReturn(Instant.ofEpochSecond(1234)) // insertion time
                .willReturn(Instant.ofEpochSecond(123456789)); // query time

        //when
        errorHolder.add("expire");

        //then
        assertThat(errorHolder.errors()).isEmpty();
    }

    private ErrorHolder defaultErrorHolder() {
        final ErrorHolder errorHolder = new ErrorHolder();
        errorHolder.now = Instant::now;
        return errorHolder;
    }
}
