package net.kemitix.slushy.app;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.enterprise.inject.Instance;

import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConversionServiceTest
        implements WithAssertions {

    private final ConversionService service = new ConversionService();

    private final Instance<AttachmentConverter> converters;
    private final Attachment attachment;

    public ConversionServiceTest(
            @Mock Instance<AttachmentConverter> converters,
            @Mock Attachment attachment
    ) {
        this.converters = converters;
        this.attachment = attachment;
    }

    @BeforeEach
    public void setUp() {
        service.attachmentConverters = converters;
    }

    @Test
    @DisplayName("When there are no converters returns the original attachment")
    public void whenNoConvertersReturnOriginal() {
        //given
        given(converters.stream()).willReturn(Stream.empty());
        //when
        Attachment result = service.convert(attachment);
        //then
        assertThat(result).isSameAs(attachment);
    }
}