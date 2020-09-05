package net.kemitix.slushy.app;

import net.kemitix.slushy.app.fileconversion.AttachmentConverter;
import net.kemitix.slushy.app.fileconversion.ConversionService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ValidFileTypes {

    // Amazon supports sending to a Kindle:
    // https://www.amazon.co.uk/gp/help/customer/display.html?nodeId=200767340
    // Excludes PDF and RTF as they are rejected despite the documentation
    public static final List<String> KINDLE_SUPPORTED = List.of(
            "docx", "doc",
            "txt",
            "azw",
            "mobi",
            "html", "htm"
    );

    private final ConversionService conversionService;
    private final Instance<AttachmentConverter> attachmentConverters;

    @Inject
    public ValidFileTypes(
            ConversionService conversionService,
            Instance<AttachmentConverter> attachmentConverters
    ) {
        this.conversionService = conversionService;
        this.attachmentConverters = attachmentConverters;
    }

    public List<String> get() {
        List<String> supported = new ArrayList<>(KINDLE_SUPPORTED);
        supported.addAll(canConvertFrom());
        return supported;
    }

    public List<String> canConvertFrom() {
        return attachmentConverters.stream()
                .flatMap(AttachmentConverter::canConvertFrom)
                .collect(Collectors.toList());
    }



}
