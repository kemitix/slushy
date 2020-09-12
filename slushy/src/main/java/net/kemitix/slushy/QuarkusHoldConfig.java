package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import net.kemitix.slushy.app.hold.HoldConfig;

@ConfigProperties(prefix = "slushy.hold")
public class QuarkusHoldConfig
        extends AbstractQuarkusListProcessingConfig
        implements HoldConfig {

}
