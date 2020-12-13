package net.kemitix.slushy.app.fileconversion;

import org.bbottema.rtftohtml.RTF2HTMLConverter;
import org.bbottema.rtftohtml.impl.RTF2HTMLConverterJEditorPane;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RTFConverterProducer {

    @Produces
    @ApplicationScoped
    Rtf2Html converter() {
        RTF2HTMLConverter converter = RTF2HTMLConverterJEditorPane.INSTANCE;
        return converter::rtf2html;
    }

}
