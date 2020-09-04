package net.kemitix.slushy.app;

import net.kemitix.slushy.app.fileconversion.ConversionService;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ValidFileTypesTest
        implements WithAssertions {

    @Mock ConversionService conversionService;

    ValidFileTypes validFileTypes;

    @BeforeEach
    public void setUp() {
        validFileTypes = new ValidFileTypes(conversionService);
    }

    @Test
    @DisplayName("includes convertible inputs")
    public void includesConvertibleInputs() {
        //given
        given(conversionService.canConvertFrom())
                .willReturn(List.of("rtf", "odt"));
        //when
        List<String> result = validFileTypes.get();
        //then
        assertThat(result).contains("rtf", "odt");
    }

}