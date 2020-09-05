package net.kemitix.slushy.app.templating;

import org.apache.camel.Header;

import java.io.IOException;

public interface SlushyTemplate {

    String render(
            @Header("SlushyTemplateName") String templateFile,
            @Header("SlushyTemplateModel") Object model
    ) throws IOException;

}
