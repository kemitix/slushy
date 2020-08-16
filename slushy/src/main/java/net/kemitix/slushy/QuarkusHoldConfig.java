package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.spi.HoldConfig;


@Setter
@Getter
@ConfigProperties(prefix = "slushy.hold")
public class QuarkusHoldConfig
        implements HoldConfig {

    String scanPeriod;
    String holdName;
    String heldName;
    String routingSlip;
    int requiredAgeHours;

}
