package net.kemitix.slushy.app;

import net.kemitix.slushy.app.fileconversion.ConversionService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ValidFileTypes {

    // Amazon supports sending to a Kindle:
    // https://www.amazon.co.uk/gp/help/customer/display.html?nodeId=200767340
    public static final List<String> KINDLE_SUPPORTED = List.of(
            "docx", "doc",
            "pdf",
            "txt",
            "azw",
            "mobi",
            "html", "htm",
            "rtf"
    );

    private final ConversionService conversionService;

    @Inject
    public ValidFileTypes(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public List<String> get() {
        List<String> supported = new ArrayList<>(KINDLE_SUPPORTED);
        supported.addAll(conversionService.canConvertFrom());
        return supported;
    }

}
