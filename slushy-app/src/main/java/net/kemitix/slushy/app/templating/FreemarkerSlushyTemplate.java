package net.kemitix.slushy.app.templating;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.java.Log;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Log
@ApplicationScoped
public class FreemarkerSlushyTemplate
        implements SlushyTemplate {

    private final Configuration configuration;

    @Inject
    public FreemarkerSlushyTemplate(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String render(
            @Header("SlushyTemplateName") String templateFile,
            @Header("SlushyTemplateModel") Object model
    ) throws IOException {
        log.info("rendering " + templateFile);
        Template template = configuration.getTemplate(templateFile);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            template.process(model, new OutputStreamWriter(outputStream));
            return outputStream.toString();
        } catch (TemplateException e) {
            throw new RuntimeException(
                    "Error rendering template for " + templateFile, e);
        }
    }
}
