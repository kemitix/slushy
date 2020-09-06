package net.kemitix.slushy.app.fileconversion;

import net.kemitix.slushy.app.Attachment;
import net.kemitix.slushy.app.AttachmentDirectory;
import net.kemitix.slushy.app.LocalAttachment;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.enterprise.inject.Instance;

import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConvertAttachmentTest
        implements WithAssertions {

    private final Instance<AttachmentConverter> converters;
    private final LocalAttachment attachment;
    private final AttachmentDirectory attachmentDirectory;

    private ConvertAttachment service;

    public ConvertAttachmentTest(
            @Mock Instance<AttachmentConverter> converters,
            @Mock LocalAttachment attachment,
            @Mock AttachmentDirectory attachmentDirectory
            ) {
        this.converters = converters;
        this.attachment = attachment;
        this.attachmentDirectory = attachmentDirectory;
    }

    @BeforeEach
    public void setUp() {
        service = new ConvertAttachment(converters, attachmentDirectory);
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