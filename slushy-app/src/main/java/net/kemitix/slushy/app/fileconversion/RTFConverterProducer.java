package net.kemitix.slushy.app.fileconversion;

import org.bbottema.rtftohtml.RTF2HTMLConverter;
import org.bbottema.rtftohtml.impl.RTF2HTMLConverterRFCCompliant;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RTFConverterProducer {

    @Produces
    @ApplicationScoped
    RTF2HTMLConverter converter() {
        return RTF2HTMLConverterRFCCompliant.INSTANCE;
    }

}
