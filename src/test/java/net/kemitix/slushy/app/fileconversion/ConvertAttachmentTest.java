package net.kemitix.slushy.app.fileconversion;

import net.kemitix.slushy.app.Submission;
import net.kemitix.trello.Attachment;
import net.kemitix.trello.AttachmentDirectory;
import net.kemitix.trello.LocalAttachment;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.enterprise.inject.Instance;

import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConvertAttachmentTest
        implements WithAssertions {

    private final Instance<AttachmentConverter> converters;
    private final LocalAttachment attachment;
    private final AttachmentDirectory attachmentDirectory;

    private ConvertAttachment service;
    private Submission submission;

    public ConvertAttachmentTest(
            @Mock Instance<AttachmentConverter> converters,
            @Mock LocalAttachment attachment,
            @Mock AttachmentDirectory attachmentDirectory,
            @Mock Submission submission
    ) {
        this.converters = converters;
        this.attachment = attachment;
        this.attachmentDirectory = attachmentDirectory;
        this.submission = submission;
    }

    @BeforeEach
    public void setUp() {
        service = new ConvertAttachment();
        service.attachmentConverters = converters;
        service.attachmentDirectory = attachmentDirectory;
    }

    @Test
    @DisplayName("When there are no converters returns the original attachment")
    public void whenNoConvertersReturnOriginal() {
        //given
        given(converters.stream()).willReturn(Stream.empty());
        //when
        Attachment result = service.convert(attachment, submission);
        //then
        assertThat(result).isSameAs(attachment);
    }
}
