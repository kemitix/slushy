package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.multisub.MultiSubConfig;

@Setter
@Getter
@ConfigProperties(prefix = "slushy.multisub")
public class QuarkusMultiSubConfig
        implements MultiSubConfig {

    String lists;

}
