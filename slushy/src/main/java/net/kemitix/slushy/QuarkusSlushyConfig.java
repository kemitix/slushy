package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.spi.SlushyConfig;

@Setter
@Getter
@ConfigProperties(prefix = "slushy")
public class QuarkusSlushyConfig
        implements SlushyConfig {

    String boardName;

}
