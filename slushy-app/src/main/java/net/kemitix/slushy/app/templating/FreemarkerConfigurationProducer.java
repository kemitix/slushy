package net.kemitix.slushy.app.templating;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import net.kemitix.slushy.app.SlushyConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class FreemarkerConfigurationProducer {

    @Produces
    @ApplicationScoped
    public static Configuration configuration() throws IOException {
        Configuration cfg = new Configuration((Configuration.VERSION_2_3_30));
        cfg.setDirectoryForTemplateLoading(
                new File(SlushyConfig.class.getResource("").getFile()));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        return cfg;
    }

}
