package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.archiver.ArchiverConfig;


@Setter
@Getter
@ConfigProperties(prefix = "slushy.archiver")
public class QuarkusArchiverConfig
        extends AbstractQuarkusListProcessingConfig
        implements ArchiverConfig {

}
