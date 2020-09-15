package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import net.kemitix.slushy.app.email.EmailConfig;

@ConfigProperties(prefix = "slushy.email")
public class QuarkusEmailConfig
        extends AbstractQuarkusRetryConfig
        implements EmailConfig {

}
