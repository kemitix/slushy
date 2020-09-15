package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import net.kemitix.slushy.app.reject.RejectConfig;

@ConfigProperties(prefix = "slushy.reject")
public class QuarkusRejectConfig
        extends AbstractQuarkusListProcessingConfig
        implements RejectConfig {

}
