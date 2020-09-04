package net.kemitix.slushy.app.fileconversion;

import org.odftoolkit.odfdom.converter.xhtml.XHTMLConverter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class OdfConverterProducer {

    @Produces
    @ApplicationScoped
    XHTMLConverter xhtmlConverter() {
        return XHTMLConverter.getInstance();
    }

}
