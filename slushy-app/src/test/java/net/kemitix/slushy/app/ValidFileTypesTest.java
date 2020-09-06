package net.kemitix.slushy.app;

import net.kemitix.slushy.app.fileconversion.AttachmentConverter;
import net.kemitix.slushy.app.fileconversion.ConvertAttachment;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.enterprise.inject.Instance;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ValidFileTypesTest
        implements WithAssertions {

    @Mock
    ConvertAttachment convertAttachment;

    ValidFileTypes validFileTypes;

    @Mock Instance<AttachmentConverter> attachmentConverters;
    @Mock AttachmentConverter attachmentConverter;

    @BeforeEach
    public void setUp() {
        validFileTypes = new ValidFileTypes(convertAttachment, attachmentConverters);
    }

    @Test
    @DisplayName("includes convertible inputs")
    public void includesConvertibleInputs() {
        //given
        given(attachmentConverters.stream())
                .willReturn(Stream.of(attachmentConverter));
        given(attachmentConverter.canConvertFrom())
                .willReturn(Stream.of("rtf", "odt"));
        //when
        List<String> result = validFileTypes.get();
        //then
        assertThat(result).contains("rtf", "odt");
    }

}