package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.reader.ReaderConfig;

@Setter
@Getter
@ConfigProperties(prefix = "slushy.reader")
public class QuarkusReaderConfig
        implements ReaderConfig {

    String scanPeriod;
    String sourceList;
    String targetList;
    String routingSlip;
    int requiredAgeHours;

}
