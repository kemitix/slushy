package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.hold.HoldConfig;


@Setter
@Getter
@ConfigProperties(prefix = "slushy.hold")
public class QuarkusHoldConfig
        extends AbstractQuarkusListProcessingConfig
        implements HoldConfig {

}
